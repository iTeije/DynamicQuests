package com.cachedcloud.dynamicquests.quests;

import com.cachedcloud.dynamicquests.messaging.MessageModule;
import com.cachedcloud.dynamicquests.messaging.StorageKey;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.ObjectiveModule;
import com.cachedcloud.dynamicquests.quests.gui.MainQuestGui;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.RewardModule;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Commands;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class QuestModule implements TerminableModule {

  // SQL statements
  private static final String CREATE_QUESTS_TABLE = "CREATE TABLE IF NOT EXISTS quests (" +
      "`uuid` varchar(36) NOT NULL, " +
      "`name` varchar(64) NOT NULL, " +
      "`description` TEXT NOT NULL)";
  private static final String GET_QUESTS = "SELECT * FROM `quests`";
  private static final String CREATE_QUEST = "INSERT INTO `quests` (`uuid`, `name`, `description`) VALUES (?, ?, ?)";
  private static final String UPDATE_QUEST = "UPDATE `quests` SET `name` = ?, `description` = ? WHERE `uuid` = ?";

  // Constructor params
  private final Sql sql;
  private final MessageModule messageModule;
  private final RewardModule rewardModule;
  private final ObjectiveModule objectiveModule;

  // List of all quests
  private boolean initialized = false;
  private final List<Quest> quests = new ArrayList<>();

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    // Create table and then query all quests (why? because concurrency will destroy
    sql.executeAsync(CREATE_QUESTS_TABLE).thenRunSync(this::initializeQuests);

    // Create main quests command
    Commands.create()
        .assertPlayer()
        .handler(cmd -> {
          // Check if plugin has been initialized
          if (initialized) {
            // Open quest GUI
            new MainQuestGui(cmd.sender(), this).open();
          } else {
            // Send error message
            cmd.reply(messageModule.getAndFormat(StorageKey.PENDING_LOAD_ERROR));
          }
        }).registerAndBind(consumer, "quest", "quests");
  }

  public List<Quest> getQuests() {
    return this.quests;
  }

  private void initializeQuests() {
    // Fetch all quests from the database
    sql.queryAsync(GET_QUESTS, preparedStatement -> {
    }, resultSet -> {
      // Parse all rows
      List<Quest> quests = new ArrayList<>();
      while (resultSet.next()) {
        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        // Create quest instance and add it to the temporary list
        quests.add(new Quest(uuid, name, Arrays.asList(description.split("\n"))));
      }
      return quests;
    }).thenAcceptSync(optionalList -> {
      List<Quest> quests = optionalList.orElse(new ArrayList<>());

      // Get rewards for all quests
      List<CompletableFuture<Void>> futures = quests.stream()
          .map(quest -> CompletableFuture.runAsync(() -> rewardModule.loadAttribute(quest))
              .thenRunAsync(() -> objectiveModule.loadAttribute(quest))
          )
          .toList();

      // Create a collection of all queued reward load operations
      CompletableFuture<Void> allQueued = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

      allQueued.thenRun(() -> {
        // Cache all quests
        this.quests.addAll(optionalList.orElse(new ArrayList<>()));
        initialized = true;

        // Log
        Bukkit.getLogger().info("Quests and rewards are fully initialized.");
      });
    });
  }
}
