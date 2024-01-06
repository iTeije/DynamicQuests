package com.cachedcloud.dynamicquests.quests;

import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.Reward;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    return objectives.stream().filter(o -> o.getUuid() == objectiveUuid).findFirst();
  }

}
