package com.cachedcloud.dynamicquests.quests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public abstract class Quest {

  private final UUID uuid;
  private String name;
  private List<String> description;

}
