package com.cachedcloud.dynamicquests.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StorageKey {

  PENDING_LOAD_ERROR("pending-load-error"),
  JOIN_QUEST_PROGRESS("join-quest-progress"),
  JOIN_QUEST_EXPIRED("join-quest-expired"),
  JOIN_QUEST_AVAILABLE("join-quest-available"),
  OBJECTIVE_COMPLETE("objective-complete");

  private final String path;
}
