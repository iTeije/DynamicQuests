package com.cachedcloud.dynamicquests.quests;

import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.Reward;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Quest {

  private final UUID uuid;
  private String name;
  private List<String> description;

  private final List<Reward> rewards = new ArrayList<>();
  private final List<Objective> objectives = new ArrayList<>();

  public Optional<Objective> getObjective(UUID objectiveUuid) {
    return objectives.stream().filter(o -> o.getUuid().equals(objectiveUuid)).findFirst();
  }

  public void issueRewards(Player player) {
    this.rewards.forEach(reward -> reward.giveReward(player));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Quest quest = (Quest) o;

    return uuid.equals(quest.uuid);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }
}
