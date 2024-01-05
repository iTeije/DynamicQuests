package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.Factory;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.types.ConsoleCommandReward;
import org.json.JSONObject;

import java.util.UUID;

public class RewardFactory implements Factory {

  public Reward getAttribute(String type, UUID uuid, String name, JSONObject attributes) {
    if (type.equalsIgnoreCase("consolecommand")) {
      return new ConsoleCommandReward(uuid, name, attributes);
    }
    return null;
  }

}
