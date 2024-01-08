package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.rewards.types.ConsoleCommandReward;
import com.cachedcloud.dynamicquests.utils.TriFunction;
import lombok.AllArgsConstructor;
import org.json.JSONObject;

import java.util.UUID;
import java.util.function.Supplier;

@AllArgsConstructor
public enum RewardType {

  CONSOLE_COMMAND(ConsoleCommandReward::new, () ->
      new ConsoleCommandReward(UUID.randomUUID(), "&7Console Command Reward", new JSONObject()
          .put("command", "tell %1$s Hello").put("completion_message", "&aYou won!")
      )
  );

  private final TriFunction<UUID, String, JSONObject, Reward> generate;
  private final Supplier<Reward> createDefault;

  public Reward createInstance(UUID uuid, String name, JSONObject json) {
    return generate.apply(uuid, name, json);
  }

  public Reward createDefault() {
    return createDefault.get();
  }

}
