package com.cachedcloud.dynamicquests.quests.tracking;

import com.cachedcloud.dynamicquests.messaging.MessageModule;
import com.cachedcloud.dynamicquests.messaging.StorageKey;
import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.QuestModule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.utils.Players;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This module ensures that quest (and therefore objective-) progress is saved.
 * It will NOT store the quest history, and thus players will be able to replay
 * any quest as many time as they please.
 * note: renamed from PlayerModule to ProgressModule
 */
@RequiredArgsConstructor
public class ProgressModule implements TerminableModule {

  // SQL statements
  private static final String CREATE_PROGRESS_TABLE = "CREATE TABLE IF NOT EXISTS quest_progress (" +
      "`player_uuid` varchar(36) NOT NULL, " +
      "`quest_uuid` varchar(64) NOT NULL, " +
      "`data` TEXT NOT NULL, " +
      "PRIMARY KEY (`player_uuid`))";
  private static final String GET_PROGRESS = "SELECT * FROM `quest_progress` WHERE `player_uuid` = ?";
  private static final String CREATE_PROGRESS = "INSERT INTO `quest_progress` (`player_uuid`, `quest_uuid`, `data`) VALUES (?, ?, ?)";
  private static final String UPDATE_QUEST_PROGRESS = "UPDATE `quest_progress` SET `quest_uuid` = ?, `data` = ? WHERE `player_uuid` = ?";
  private static final String DELETE_QUEST_PROGRESS = "DELETE FROM `quest_progress` WHERE `player_uuid` = ?";

  // Constructor params
  private final Sql sql;
  private final QuestModule questModule;
  @Getter
  private final MessageModule messageModule;

  // QuestProgress instance that belongs to a certain player
  private final Map<UUID, QuestProgress> progressMap = new ConcurrentHashMap<>();

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    // Create table
    this.sql.executeAsync(CREATE_PROGRESS_TABLE);
    QuestProgress.setProgressModule(this); // :')


    // Get progress of ongoing quest when logging in
    Events.subscribe(PlayerJoinEvent.class)
        .handler(event -> {
          // Cancel join message
          event.joinMessage(Component.empty());

          // Load player progress on join
          this.loadProgress(event.getPlayer().getUniqueId()).thenAcceptSync(progress -> {
            // If the player does not have an active quest, prompt them to start one
            Player player = event.getPlayer();
            if (progress == null) {
              Players.msg(player, messageModule.getAndFormat(StorageKey.JOIN_QUEST_AVAILABLE, player.getName()));
              return;
            }

            // Calculate total progress and required progress
            int totalRequired = 0;
            int totalComplete = 0;

            // Loop through the progress entries for all objectives
            for (QuestProgress.ProgressEntry entry : progress.getProgress().values()) {
              totalRequired += entry.getRequirement();
              totalComplete += entry.getProgress().intValue();
            }
            totalRequired = Math.max(totalRequired, 1); // For debugging in case there are no objectives

            // Calculate percentage
            int percentageComplete = (int) (((double) totalComplete / totalRequired) * 100);
            System.out.println(percentageComplete);

            // Send message
            Players.msg(
                player,
                messageModule.getAndFormat(
                    StorageKey.JOIN_QUEST_PROGRESS,
                    percentageComplete,
                    progress.getQuest().getName(),
                    player.getName()
                )
            );
          });
        }).bindWith(consumer);

    // Store progress of ongoing quest when logging out (and remove it from cache)
    Events.subscribe(PlayerQuitEvent.class)
        .filter(event -> this.progressMap.containsKey(event.getPlayer().getUniqueId()))
        .handler(event -> {
          // Save data and then remove the quest progress from the cache
          this.updateQuestProgress(event.getPlayer().getUniqueId()).thenAcceptSync(progress -> {
            if (progress == null) return;

            this.progressMap.remove(event.getPlayer().getUniqueId());
            this.cancelTracking(event.getPlayer().getUniqueId(), progress.getQuest());
          });
        }).bindWith(consumer);


    // Store all progress occasionally
    Schedulers.async().runRepeating(task -> {
      this.progressMap.forEach((uuid, progress) -> {
        this.updateQuestProgress(uuid);
      });
    }, 5, TimeUnit.MINUTES, 5, TimeUnit.MINUTES);

    // Make sure all data is saved on shutdown
    consumer.bind(() -> {
      this.progressMap.forEach((uuid, progress) -> {
        this.updateQuestProgress(uuid);
      });
    });
  }

  /**
   * Handle the tracking part of starting a quest
   *
   * @param player the player that is starting a quest
   * @param quest  the quest to start
   * @return whether the plugin has started tracking the objectives for the player
   */
  public boolean startTracking(Player player, Quest quest) {
    // Check if the player is already doing a quest
    if (this.progressMap.containsKey(player.getUniqueId())) return false;

    // Create questprogress instance
    QuestProgress progress = new QuestProgress(player.getUniqueId(), quest);

    // Store questprogress
    this.progressMap.put(player.getUniqueId(), progress);

    // Push to database
    this.sql.executeAsync(CREATE_PROGRESS, ps -> {
      ps.setString(1, player.getUniqueId().toString());
      ps.setString(2, quest.getUuid().toString());
      ps.setString(3, progress.serialize());
    });

    return true;
  }

  /**
   * Delete the progress of a certain player
   * @param playerUuid the player to remove the data for
   */
  public void deleteProgress(UUID playerUuid, Quest quest) {
    // Remove progress from cache
    this.progressMap.remove(playerUuid);

    this.cancelTracking(playerUuid, quest);

    // Delete entry from database
    this.sql.executeAsync(DELETE_QUEST_PROGRESS, ps -> {
      ps.setString(1, playerUuid.toString());
    });
  }

  public void cancelTracking(UUID playerUuid, Quest quest) {
    // Loop through objectives and remove player uuid from tracked list
    quest.getObjectives().forEach(objective -> {
      objective.unTrackPlayer(playerUuid);
    });
  }

  /**
   * Save the quest progress to the database
   *
   * @param playerUuid the player to save the data for
   */
  private Promise<QuestProgress> updateQuestProgress(UUID playerUuid) {
    // Get quest progress for player
    QuestProgress progress = this.progressMap.get(playerUuid);

    // Check if progress is present
    if (progress == null) return Promise.empty();

    // Update progress
    return sql.executeAsync(UPDATE_QUEST_PROGRESS, ps -> {
      ps.setString(1, progress.getQuest().getUuid().toString());
      ps.setString(2, progress.serialize());
      ps.setString(3, playerUuid.toString());
    }).thenApplyAsync(nothing -> progress);
  }

  /**
   * Load progress from database when the player joins
   *
   * @param playerUuid the player to load the information for
   */
  private Promise<QuestProgress> loadProgress(UUID playerUuid) {
    // Fetch quest progress from database
    return sql.queryAsync(GET_PROGRESS, ps -> {
      ps.setString(1, playerUuid.toString());
    }, resultSet -> {
      if (!resultSet.next()) return null;

      // Get quest uuid and player progress from the resultset
      UUID questUuid = UUID.fromString(resultSet.getString("quest_uuid"));
      JSONObject json = new JSONObject(resultSet.getString("data"));

      // Get quest
      Quest quest = this.questModule.getQuest(questUuid);
      if (quest == null) return null; // this can only happen when quests are deleted while players still use it

      return new QuestProgress(playerUuid, quest, json);
    }).thenApplyAsync(progressOptional -> {
      // Check if any progress was fetched
      if (progressOptional.isEmpty()) return null;

      // Store progress
      QuestProgress progress = progressOptional.get();
      this.progressMap.put(playerUuid, progress);
      return progress;
    });
  }

  /**
   * Handle the completion of an objective, which is triggered from the QuestProgress object
   *
   * @param playerUuid player uuid for who to handle the completion
   */
  public void handleObjectiveComplete(UUID playerUuid) {
    // Get player instance from uuid and send message
    Players.get(playerUuid).ifPresent(player -> {
      Players.msg(player, messageModule.getAndFormat(StorageKey.OBJECTIVE_COMPLETE));
    });

    // Save current quest progress
    this.updateQuestProgress(playerUuid);
  }

  public Quest getCurrentQuest(UUID playerUuid) {
    // Check if player has an ongoing quest at all
    if (!(this.progressMap.containsKey(playerUuid))) return null;

    // Get quest progress for player
    QuestProgress progress = this.progressMap.get(playerUuid);

    // Return quest
    return progress.getQuest();
  }
}
