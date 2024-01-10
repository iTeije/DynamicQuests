package com.cachedcloud.dynamicquests.events;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.tracking.QuestProgress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class PlayerQuestChangeStatusEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();

  private final UUID playerUuid;
  private final Quest quest;
  private final QuestProgress progress;
  private final Status status;

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public enum Status {
    START,
    CONTINUE,
    COMPLETE,
    CANCEL
  }
}
