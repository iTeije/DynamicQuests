package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.attributes.Factory;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.types.KillEntityObjective;
import org.json.JSONObject;

import java.util.UUID;

public class ObjectiveFactory implements Factory<Objective> {

  public Objective getAttribute(String type, UUID uuid, String name, JSONObject json) {
    if (type.equalsIgnoreCase("killentity")) {
      return new KillEntityObjective(uuid, name, json);
    }
    // todo
    return null;
  }

}
