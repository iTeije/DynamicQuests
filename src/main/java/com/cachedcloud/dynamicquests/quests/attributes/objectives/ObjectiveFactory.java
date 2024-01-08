package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.attributes.Factory;
import org.bukkit.Bukkit;
import org.json.JSONException;
import org.json.JSONObject;

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
    } catch (IllegalArgumentException exception) {
      Bukkit.getLogger().warning("ObjectiveType '" + type.toUpperCase() + "' does not exist.");
    } catch (JSONException exception) {
      Bukkit.getLogger().warning("Objective has invalid json data. (type " + type + ", uuid " + uuid.toString() +
          ", name " + name + ", json: " + json.toString() + ")");
      // Printing stack trace for a detailed description of the missing json key
      exception.printStackTrace();
    }

    return null;
  }

}
