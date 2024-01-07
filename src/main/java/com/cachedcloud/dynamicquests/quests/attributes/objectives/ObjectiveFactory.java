package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.attributes.Factory;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.types.BreakBlockObjective;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.types.KillEntityObjective;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.types.PlaceBlockObjective;
import org.json.JSONObject;

import java.util.UUID;

public class ObjectiveFactory implements Factory<Objective> {

  public Objective getAttribute(String type, UUID uuid, String name, JSONObject json) {
    if (type.equalsIgnoreCase("killentity")) {
      return new KillEntityObjective(uuid, name, json);
    } else if (type.equalsIgnoreCase("breakblock")) {
      return new BreakBlockObjective(uuid, name, json);
    } else if (type.equalsIgnoreCase("placeblock")) {
      return new PlaceBlockObjective(uuid, name, json);
    }

    return null;
  }

}
