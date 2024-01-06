package com.cachedcloud.dynamicquests.quests.attributes.objectives.types;

import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.json.JSONObject;

import java.util.UUID;

public class KillEntityObjective extends Objective {

  private final EntityType type;
  private final int amount;

  public KillEntityObjective(UUID uuid, String name, JSONObject json) {
    super(uuid, name, json);
    this.type = EntityType.valueOf(json.getString("type").toUpperCase());
    this.amount = json.getInt("amount");
  }

  @Override
  public void registerListeners(TerminableConsumer consumer) {
    Events.subscribe(EntityDamageByEntityEvent.class)
        .filter(event -> event.getEntity().getType() == this.type)
        .filter(event -> event.getDamager().getType() == EntityType.PLAYER)
        .filter(event -> super.getTrackedPlayers().containsKey(event.getDamager().getUniqueId()))
        .handler(event -> {

        }).bindWith(consumer);
  }
}
