package com.cachedcloud.dynamicquests.quests.attributes.objectives.types;

import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import me.lucko.helper.Events;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.json.JSONObject;

import java.util.UUID;

public class KillEntityObjective extends Objective {

  private final EntityType type;

  public KillEntityObjective(UUID uuid, String name, JSONObject json) {
    super(uuid, name, json);
    this.type = EntityType.valueOf(json.getString("entityType").toUpperCase());
  }

  @Override
  public void registerListeners(TerminableConsumer consumer) {
    // Track entity kills
    Events.subscribe(EntityDamageByEntityEvent.class)
        .filter(EventFilters.ignoreCancelled())
        .filter(event -> event.getEntity().getType() == this.type)
        .filter(event -> event.getDamager().getType() == EntityType.PLAYER)
        .filter(event -> ((Damageable) event.getEntity()).getHealth() - event.getFinalDamage() <= 0)
        .filter(event -> isTracking(event.getDamager().getUniqueId()))
        .handler(event -> {
          // Increment progress
          super.incrementProgress(event.getDamager().getUniqueId(), 1);
        }).bindWith(consumer);
  }
}
