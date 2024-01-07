package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.rewards.types.ConsoleCommandReward;
import com.cachedcloud.dynamicquests.utils.TriFunction;
import lombok.AllArgsConstructor;
import org.json.JSONObject;

import java.util.UUID;

@AllArgsConstructor
public enum RewardType {

  CONSOLE_COMMAND(ConsoleCommandReward::new);

  private final TriFunction<UUID, String, JSONObject, Reward> generate;

  public Reward createInstance(UUID uuid, String name, JSONObject json) {
    return generate.apply(uuid, name, json);
  }

}
