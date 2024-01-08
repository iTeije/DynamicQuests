package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.attributes.BaseAttribute;
import com.cachedcloud.dynamicquests.quests.tracking.QuestProgress;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
public abstract class Objective implements BaseAttribute {

  private Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

  private final UUID uuid;
  private final JSONObject json; // objective attributes
  private final ObjectiveType type;

  @Setter private String name;
  private int requirement;

  private final Map<UUID, QuestProgress> trackedPlayers = new HashMap<>();

  public Objective(UUID uuid, String name, JSONObject json, ObjectiveType type) {
    this.uuid = uuid;
    this.name = name;
    this.json = json;
    parseJson(json);
    this.requirement = json.getInt("amount");
    this.type = type;
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

  public String getType() {
    return this.type.name();
  }

  @Override
  public void updateAttribute(String key, String value) throws JSONException {
    Object oldValue = json.has(key) ? json.get(key) : null;
    // Update the json object
    if (value == null) {
      this.json.remove(key);
    } else if (NUMERIC_PATTERN.matcher(value).matches()) {
      this.json.put(key, Integer.parseInt(value));
    } else {
      this.json.put(key, value);
    }

    try {
      // Check if key is 'amount'
      if (key.equals("amount")) requirement = Integer.parseInt(value);

      parseJson(this.json);
    } catch (JSONException | NullPointerException | NoSuchElementException | NumberFormatException e) {
      Bukkit.getLogger().warning("Objective " + uuid.toString() + " has invalid json data because of the " +
          key + ":" + value + " change.");
      if (oldValue != null) {
        this.json.put(key, oldValue);
      } else this.json.remove(key);
    }
  }

  public abstract void registerListeners(TerminableConsumer consumer);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Objective objective = (Objective) o;

    return uuid.equals(objective.uuid);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }
}
