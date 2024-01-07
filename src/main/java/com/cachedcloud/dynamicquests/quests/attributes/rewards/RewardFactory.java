package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.Factory;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.types.ConsoleCommandReward;
import org.json.JSONObject;

import java.util.UUID;

public class RewardFactory implements Factory<Reward> {

  public Reward getAttribute(String type, UUID uuid, String name, JSONObject attributes) {
    if (type.equalsIgnoreCase("consolecommand")) {
      return new ConsoleCommandReward(uuid, name, attributes);
    }
    // If you want rewards that implement an API, you can write these yourself.
    // The only thing that's configurable in game is a command anyway.

    return null;
  }

}
