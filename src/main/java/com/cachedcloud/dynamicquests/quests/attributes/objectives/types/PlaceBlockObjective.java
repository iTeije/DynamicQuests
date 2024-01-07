package com.cachedcloud.dynamicquests.quests.attributes.objectives.types;

import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import me.lucko.helper.Events;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.JSONObject;

import java.util.UUID;

public class PlaceBlockObjective extends Objective {

  private final Material blockType;

  public PlaceBlockObjective(UUID uuid, String name, JSONObject json) {
    super(uuid, name, json);
    this.blockType = Material.valueOf(json.getString("material").toUpperCase());
  }

  @Override
  public void registerListeners(TerminableConsumer consumer) {
    // Track block placement
    Events.subscribe(BlockPlaceEvent.class)
        .filter(EventFilters.ignoreCancelled())
        .filter(event -> event.getBlockPlaced().getType() == blockType)
        .filter(event -> isTracking(event.getPlayer()))
        .handler(event -> {
          // Increment progress
          super.incrementProgress(event.getPlayer().getUniqueId(), 1);
        }).bindWith(consumer);
  }
}
