package com.cachedcloud.dynamicquests.rewards;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public abstract class Reward {

  private final UUID uuid;
  private final String name;

  public abstract void giveReward(Player player);

}
