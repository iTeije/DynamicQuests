package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.attributes.BaseAttribute;
import com.cachedcloud.dynamicquests.quests.tracking.QuestProgress;
import lombok.Getter;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class Objective implements BaseAttribute {

  private final UUID uuid;
  private String name;
  private JSONObject json; // objective attributes
  private int requirement;

  private final Map<UUID, QuestProgress> trackedPlayers = new HashMap<>();

  public Objective(UUID uuid, String name, JSONObject json) {
    this.uuid = uuid;
    this.name = name;
    this.json = json;
    this.requirement = json.getInt("amount");
  }

  public void trackPlayer(UUID player, QuestProgress progress) {
    this.trackedPlayers.put(player, progress);
  }

  public void incrementProgress(UUID player) {
    this.incrementProgress(player, 1);
  }

  public void incrementProgress(UUID player, int amount) {
    this.trackedPlayers.get(player).incrementProgress(this, amount);
  }

  public void unTrackPlayer(UUID player) {
    this.trackedPlayers.remove(player);
  }

  public boolean isTracking(Player player) {
    return isTracking(player.getUniqueId());
  }

  public boolean isTracking(UUID playerUuid) {
    return this.trackedPlayers.containsKey(playerUuid);
  }

  public abstract void registerListeners(TerminableConsumer consumer);

}
