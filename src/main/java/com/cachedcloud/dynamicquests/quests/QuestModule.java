package com.cachedcloud.dynamicquests.quests;

import com.cachedcloud.dynamicquests.messaging.MessageModule;
import com.cachedcloud.dynamicquests.messaging.StorageKey;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.ObjectiveModule;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.RewardModule;
import com.cachedcloud.dynamicquests.quests.gui.MainQuestGui;
import com.cachedcloud.dynamicquests.quests.gui.admin.MainAdminGui;
import com.cachedcloud.dynamicquests.quests.tracking.ProgressModule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Commands;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
  private static final String DELETE_QUEST = "DELETE FROM `quests` WHERE `uuid` = ?";

  // Constructor params
  private final Sql sql;
  @Getter
  private final MessageModule messageModule;
  @Getter
  private final RewardModule rewardModule;
  @Getter
  private final ObjectiveModule objectiveModule;

  // List of all quests
  private boolean initialized = false;
  private final Map<UUID, Quest> quests = new HashMap<>();

  // Progress module (initialized here)
  private ProgressModule progressModule;

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    // Initialize ProgressModule
    progressModule = consumer.bindModule(new ProgressModule(sql, this, messageModule));

    // Create table and then query all quests (why? because concurrency will destroy
    sql.executeAsync(CREATE_QUESTS_TABLE).thenRunSync(this::initializeQuests);

    // Create main quests command
    Commands.create()
        .assertPlayer()
        .handler(cmd -> {
          // Check if plugin has been initialized
          if (initialized) {
            // Open quest GUI
            new MainQuestGui(cmd.sender(), progressModule, this).open();
          } else {
            // Send error message
            cmd.reply(messageModule.getAndFormat(StorageKey.PENDING_LOAD_ERROR));
          }
        }).registerAndBind(consumer, "quest", "quests");

    // Create admin command
    Commands.create()
        .assertPlayer()
        .assertPermission("quests.admin")
        .handler(cmd -> {
          // Check if plugin has been initialized
          if (initialized) {
            // Open admin GUI
            new MainAdminGui(cmd.sender(), this).open();
          } else {
            // Send error message
            cmd.reply(messageModule.getAndFormat(StorageKey.PENDING_LOAD_ERROR));
          }
        }).registerAndBind(consumer, "questadmin", "questsadmin");
  }

  private void initializeQuests() {
    // Fetch all quests from the database
    sql.queryAsync(GET_QUESTS, preparedStatement -> {
    }, resultSet -> {
      // Parse all rows
      Map<UUID, Quest> quests = new HashMap<>();
      while (resultSet.next()) {
        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        // Create quest instance and add it to the temporary list
        quests.put(uuid, new Quest(uuid, name, Arrays.asList(description.split("\\\\n"))));
      }
      return quests;
    }).thenAcceptSync(optionalList -> {
      Map<UUID, Quest> quests = optionalList.orElse(new HashMap<>());

      // Get rewards for all quests
      List<CompletableFuture<Void>> futures = quests.values().stream()
          .map(quest -> CompletableFuture.runAsync(() -> rewardModule.loadAttribute(quest))
              .thenRunAsync(() -> objectiveModule.loadAttribute(quest))
          )
          .toList();

      // Create a collection of all queued reward load operations
      CompletableFuture<Void> allQueued = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

      allQueued.thenRun(() -> {
        // Cache all quests
        this.quests.putAll(quests);
        initialized = true;

        // Log
        Bukkit.getLogger().info("Quests and its attributes are fully initialized.");
      });
    });
  }

  /* Create a new Quest object and put it in the database */
  public Quest createEmptyQuest() {
    // Create new quest object
    Quest quest = new Quest(UUID.randomUUID(), "&eUnnamed Quest", List.of("&7Empty description"));

    // Create new database row
    sql.executeAsync(CREATE_QUEST, ps -> {
      ps.setString(1, quest.getUuid().toString());
      ps.setString(2, quest.getName());
      ps.setString(3, String.join("\\n", quest.getDescription()));
    });

    // Add to cache
    this.quests.put(quest.getUuid(), quest);

    return quest;
  }

  /* Update the database information on a Quest object */
  public void updateQuest(Quest quest) {
    sql.executeAsync(UPDATE_QUEST, ps -> {
      ps.setString(1, quest.getName());
      ps.setString(2, String.join("\\n", quest.getDescription()));
      ps.setString(3, quest.getUuid().toString());
    });
  }

  /* Delete a quest from the cache and from the database */
  public void deleteQuest(UUID questUuid) {
    // Remove from db
    sql.executeAsync(DELETE_QUEST, ps -> {
      ps.setString(1, questUuid.toString());
    });

    // Remove locally
    Quest quest = this.quests.remove(questUuid);

    // Delete and cancel player progress for this quest
    this.progressModule.deleteProgressForQuest(quest);
  }

  public Collection<Quest> getQuests() {
    return this.quests.values();
  }

  public List<Quest> getValidQuests() {
    return this.quests.values().stream().filter(quest -> {
      return quest.getObjectives().size() > 0 && quest.getRewards().size() > 0 && quest.getDescription().size() > 0;
    }).collect(Collectors.toList());
  }

  public Quest getQuest(UUID questUuid) {
    return this.quests.get(questUuid);
  }
}
