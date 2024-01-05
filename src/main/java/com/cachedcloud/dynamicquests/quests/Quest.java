package com.cachedcloud.dynamicquests.quests;

import com.cachedcloud.dynamicquests.rewards.Reward;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Quest {

  private final UUID uuid;
  private String name;
  private List<String> description;

  private final List<Reward> rewards = new ArrayList<>();

  // the game design doc does not specify anything about objectives???
  // I'll add that later anyway

}
