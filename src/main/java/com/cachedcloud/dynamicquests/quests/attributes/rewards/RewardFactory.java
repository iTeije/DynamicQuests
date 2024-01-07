package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.Factory;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.util.NoSuchElementException;
import java.util.UUID;

public class RewardFactory implements Factory<Reward> {

  public Reward getAttribute(String type, UUID uuid, String name, JSONObject attributes) {
    try {
      // Get RewardType from string
      RewardType rewardType = RewardType.valueOf(type.toUpperCase());

      // Create an instance of the corresponding Reward
      return rewardType.createInstance(uuid, name, attributes);
    } catch (NoSuchElementException exception) {
      Bukkit.getLogger().info("RewardType '" + type.toUpperCase() + "' does not exist.");
    }

    return null;
  }

}
