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
import java.util.function.Supplier;

@AllArgsConstructor
public enum ObjectiveType {

  BREAK_BLOCK(BreakBlockObjective::new, () ->
      new BreakBlockObjective(UUID.randomUUID(), "&7Break Block", new JSONObject().put("amount", Integer.MAX_VALUE).put("material", "BEDROCK")),
      "amount", "material"),
  PLACE_BLOCK(PlaceBlockObjective::new, () ->
      new PlaceBlockObjective(UUID.randomUUID(), "&7Place Block", new JSONObject().put("amount", Integer.MAX_VALUE).put("material", "BEDROCK")),
      "amount", "material"),
  KILL_ENTITY(KillEntityObjective::new, () ->
      new KillEntityObjective(UUID.randomUUID(), "&7Kill Entity", new JSONObject().put("amount", Integer.MAX_VALUE).put("entityType", "ENDER_DRAGON")),
      "amount", "entityType");

  private final TriFunction<UUID, String, JSONObject, Objective> generate;
  private final Set<String> requiredArgs;
  private final Supplier<Objective> createDefault;

  ObjectiveType(TriFunction<UUID, String, JSONObject, Objective> generate, Supplier<Objective> createDefault, String... requiredArgs) {
    this.generate = generate;
    this.createDefault = createDefault;
    this.requiredArgs = Sets.newHashSet(requiredArgs);
  }

  public boolean hasRequiredArgs(JSONObject json) {
    return requiredArgs.stream().allMatch(json::has);
  }

  public Objective createInstance(UUID uuid, String name, JSONObject json) {
    return generate.apply(uuid, name, json);
  }

  public Objective createDefault() {
    return createDefault.get();
  }
}
