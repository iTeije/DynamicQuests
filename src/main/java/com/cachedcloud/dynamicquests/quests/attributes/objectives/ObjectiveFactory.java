package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.attributes.Factory;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.util.NoSuchElementException;
import java.util.UUID;

public class ObjectiveFactory implements Factory<Objective> {

  public Objective getAttribute(String type, UUID uuid, String name, JSONObject json) {
    try {
      // Get ObjectiveType from string
      ObjectiveType objectiveType = ObjectiveType.valueOf(type.toUpperCase());

      if (!objectiveType.hasRequiredArgs(json)) {
        // Log that the objective is invalid
        Bukkit.getLogger().warning("Objective with type '" + objectiveType.name() + "' has improper attributes: " + json);
        return null;
      }

      // Create an instance of the corresponding Objective
      return objectiveType.createInstance(uuid, name, json);
    } catch (NoSuchElementException exception) {
      Bukkit.getLogger().warning("ObjectiveType '" + type.toUpperCase() + "' does not exist.");
    }

    return null;
  }

}
