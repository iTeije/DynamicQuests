package com.cachedcloud.dynamicquests.tracking;

import com.cachedcloud.dynamicquests.quests.Quest;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
      "`data` TEXT NOT NULL)";
  private static final String GET_PROGRESS = "SELECT * FROM `quest_progress` WHERE `player_uuid` = ?";
  private static final String CREATE_PROGRESS = "INSERT INTO `quest_progress` (`player_uuid`, `quest_uuid`, `data`) VALUES (?, ?, ?)";
  private static final String UPDATE_QUEST_PROGRESS = "UPDATE `quest_progress` SET `quest_uuid` = ?, `data` = ? WHERE `player_uuid` = ?";

  // Constructor params
  private final Sql sql;

  // QuestProgress instance that belongs to a certain player
  private final Map<UUID, QuestProgress> progressMap = new HashMap<>();

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    // Create table
    this.sql.executeAsync(CREATE_PROGRESS_TABLE);

    // Get progress of ongoing quest when logging in
    Events.subscribe(PlayerJoinEvent.class)
        .handler(event -> {
          // todo
        }).bindWith(consumer);

    // Store progress of ongoing quest when logging out (and remove it from cache)
    Events.subscribe(PlayerQuitEvent.class)
        .filter(event -> this.progressMap.containsKey(event.getPlayer().getUniqueId()))
        .handler(event -> {
          // todo push update

          // todo remove from cache after update is complete
        }).bindWith(consumer);

    // Store all progress occasionally
    Schedulers.async().runRepeating(task -> {
      // todo
    }, 5, TimeUnit.MINUTES, 5, TimeUnit.MINUTES);
  }

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
    });

    return true;
  }

}
