package com.cachedcloud.dynamicquests.quests.attributes.objectives.types;

import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import me.lucko.helper.Events;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.json.JSONObject;

import java.util.UUID;

public class BreakBlockObjective extends Objective {

  private final Material blockType;
  private final boolean allBlocks;

  public BreakBlockObjective(UUID uuid, String name, JSONObject json) {
    super(uuid, name, json);
    if (!json.has("material")) {
      this.allBlocks = true;
      this.blockType = null;
      return;
    }

    this.blockType = Material.valueOf(json.getString("material").toUpperCase());
    this.allBlocks = false;
  }

  @Override
  public void registerListeners(TerminableConsumer consumer) {
    // Track block break
    Events.subscribe(BlockBreakEvent.class)
        .filter(EventFilters.ignoreCancelled())
        .filter(event -> this.allBlocks || event.getBlock().getType() == blockType)
        .filter(event -> isTracking(event.getPlayer()))
        .handler(event -> {
          // Increment progress
          super.incrementProgress(event.getPlayer().getUniqueId(), 1);
        }).bindWith(consumer);
  }
}

