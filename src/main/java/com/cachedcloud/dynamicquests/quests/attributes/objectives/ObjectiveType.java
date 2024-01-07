package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.attributes.objectives.types.BreakBlockObjective;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.types.KillEntityObjective;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.types.PlaceBlockObjective;
import com.cachedcloud.dynamicquests.utils.TriFunction;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.json.JSONObject;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public enum ObjectiveType {

  BREAK_BLOCK(BreakBlockObjective::new, "amount", "material"),
  PLACE_BLOCK(PlaceBlockObjective::new, "amount", "material"),
  KILL_ENTITY(KillEntityObjective::new, "amount", "entityType");

  private final TriFunction<UUID, String, JSONObject, Objective> generate;
  private final Set<String> requiredArgs;

  ObjectiveType(TriFunction<UUID, String, JSONObject, Objective> generate, String... requiredArgs) {
    this.generate = generate;
    this.requiredArgs = Sets.newHashSet(requiredArgs);
  }

  public boolean hasRequiredArgs(JSONObject json) {
    return requiredArgs.stream().allMatch(json::has);
  }

  public Objective createInstance(UUID uuid, String name, JSONObject json) {
    return generate.apply(uuid, name, json);
  }
}
