package com.cachedcloud.dynamicquests.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StorageKey {

  PENDING_LOAD_ERROR("pending-load-error"),
  JOIN_QUEST_PROGRESS("join-quest-progress"),
  JOIN_QUEST_AVAILABLE("join-quest-available"),

  QUEST_COMPLETE("quest-complete"),
  OBJECTIVE_COMPLETE("objective-complete"),
  QUEST_DELETED("quest-deleted"),

  MENU_ACTIVE_QUEST_ERROR("menu.active-quest-error"),
  MENU_QUEST_START_SUCCESS("menu.quest-start-success"),
  MENU_QUEST_CANCEL_SUCCESS("menu.quest-cancel-success");

  private final String path;
}
