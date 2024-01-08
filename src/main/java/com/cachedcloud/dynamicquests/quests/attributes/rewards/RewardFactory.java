package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.Factory;
import org.bukkit.Bukkit;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class RewardFactory implements Factory<Reward> {

  public Reward getAttribute(String type, UUID uuid, String name, JSONObject attributes) {
    try {
      // Get RewardType from string
      RewardType rewardType = RewardType.valueOf(type.toUpperCase());

      // Create an instance of the corresponding Reward
      return rewardType.createInstance(uuid, name, attributes);
    } catch (IllegalArgumentException exception) {
      Bukkit.getLogger().info("RewardType '" + type.toUpperCase() + "' does not exist.");
    } catch (JSONException exception) {
      Bukkit.getLogger().warning("Reward has invalid json data. (type " + type + ", uuid " + uuid.toString() +
          ", name " + name + ", json: " + attributes.toString() + ")");
      // Printing stack trace for a detailed description of the missing json key
      exception.printStackTrace();
    }

    return null;
  }

}
