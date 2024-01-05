package com.cachedcloud.dynamicquests.rewards;

import com.cachedcloud.dynamicquests.rewards.types.ConsoleCommandReward;
import org.json.JSONObject;

import java.util.UUID;

public class RewardFactory {

  public Reward getReward(String type, UUID uuid, String name, JSONObject attributes) {
    if (type.equalsIgnoreCase("consolecommand")) {
      return new ConsoleCommandReward(uuid, name, attributes);
    }
    return null;
  }

}
