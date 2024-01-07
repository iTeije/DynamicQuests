package com.cachedcloud.dynamicquests.quests.tracking;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.mutable.MutableInt;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class QuestProgress {

  /* hacky, temporary (or maybe not lol) solution because passing it down the constructor of every QuestProgress
  object causes *a lot* of unnecessary overhead */
  private static ProgressModule progressModule;

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
      objective.trackPlayer(playerUuid, this);
    }
  }

  public QuestProgress(UUID playerUuid, Quest quest, JSONObject data) {
    this.playerUuid = playerUuid;
    this.quest = quest;

    this.progress = new HashMap<>();
    for (String key : data.keySet()) {
      UUID objectiveUuid = UUID.fromString(key);
      int progress = data.getInt(key);

      Optional<Objective> objectiveOpt = quest.getObjective(objectiveUuid);

      // Check if objective actually exists (it would be weird if it wasn't but it could definitely happen)
      if (objectiveOpt.isEmpty()) continue;

      // Save progress
      Objective objective = objectiveOpt.get();
      this.progress.put(objectiveUuid, new ProgressEntry(progress, objective.getRequirement()));
      objective.trackPlayer(playerUuid, this);

      // Increment progress by 0 to trigger completion checks (in case the requirement was updated)
      this.incrementProgress(objective, 0);
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

    // Check if any objectives remain
    if (this.progress.size() == 0) {
      // Quest complete
      progressModule.handleQuestComplete(this.playerUuid, this);
    } else {
      // Send confirmation message to the player saying that they completed an objective
      progressModule.handleObjectiveComplete(this.playerUuid);
    }
  }

  /* In retrospect it would've been better if I had implemented some sort of Serializable object but it
    doesn't really bother me right now */
  public String serialize() {
    // Serialize progress
    JSONObject jsonObject = new JSONObject();

    this.progress.forEach((key, value) -> jsonObject.put(key.toString(), value.progress.intValue()));

    return jsonObject.toString();
  }

  public static void setProgressModule(ProgressModule module) {
    progressModule = module;
  }

  @RequiredArgsConstructor
  @Getter
  public static class ProgressEntry {
    private final MutableInt progress = new MutableInt(0);
    private final int requirement;

    public ProgressEntry(int existingProgress, int requirement) {
      this.progress.setValue(existingProgress);
      this.requirement = requirement;
    }

    public boolean add(int amount) {
      int newAmount = this.progress.addAndGet(amount);
      return newAmount >= requirement;
    }
  }
}
