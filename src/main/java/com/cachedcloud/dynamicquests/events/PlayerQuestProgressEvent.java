package com.cachedcloud.dynamicquests.events;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import com.cachedcloud.dynamicquests.quests.tracking.QuestProgress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class PlayerQuestProgressEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();

  private final UUID playerUuid;
  private final Quest quest;
  private final Objective objective;
  private final QuestProgress progress;
  private final boolean complete;

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }
}
