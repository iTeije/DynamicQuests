package com.cachedcloud.dynamicquests.tracking;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestProgress {

  private final UUID playerUuid;
  private final Quest quest;
  private final Map<UUID, ProgressEntry> progress;

  public QuestProgress(UUID playerUuid, Quest quest) {
    this.playerUuid = playerUuid;
    this.quest = quest;

    // Initialize progress map and store all objective UUID's and an object that tracks it progress
    this.progress = new HashMap<>();
    for (Objective objective : quest.getObjectives()) {
      this.progress.put(objective.getUuid(), new ProgressEntry(objective.getRequirement()));
    }
  }

  /* Increment the progress of an objective by a certain amount */
  public void incrementProgress(Objective objective, int amount) {
    // After adding the new progress, check if the requirement is met
    boolean complete = this.progress.get(objective.getUuid()).add(amount);

    // If the requirement is met, stop tracking that objective
    if (!complete) return;
    objective.unTrackPlayer(playerUuid);
    this.progress.remove(objective.getUuid());
  }

  @RequiredArgsConstructor
  private static class ProgressEntry {
    private final MutableInt progress = new MutableInt(0);
    private final int requirement;

    public boolean add(int amount) {
      int newAmount = this.progress.addAndGet(amount);
      return newAmount >= requirement;
    }
  }
}
