package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.BaseAttribute;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

@Getter
public abstract class Reward implements BaseAttribute {

  private final UUID uuid;
  private final JSONObject json; // reward attributes
  private final RewardType type;

  @Setter private String name;

  public Reward(UUID uuid, String name, JSONObject json, RewardType type) {
    this.uuid = uuid;
    this.name = name;
    this.type = type;
    this.json = json;
    parseJson(json);
  }

  public void sendRewardMessage(Player player) {
    // Send message to player in case there is one
    String msg = json.getString("completion_message");

    if (msg != null) Players.msg(player, msg);
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
    } else this.json.put(key, value);

    // Reload json attributes in reward implementation
    try {
      parseJson(this.json);
    } catch (JSONException | NullPointerException | IllegalArgumentException e) {
      Bukkit.getLogger().warning("Reward " + uuid.toString() + " has invalid json data because of the " +
          key + ":" + value + " change.");
      if (oldValue != null) {
        this.json.put(key, oldValue);
      } else this.json.remove(key);
    }
  }

  public abstract void giveReward(Player player);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Reward reward = (Reward) o;

    return uuid.equals(reward.uuid);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }
}
