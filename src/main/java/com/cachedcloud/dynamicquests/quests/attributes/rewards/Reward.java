package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.BaseAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.UUID;

@AllArgsConstructor
@Getter
public abstract class Reward implements BaseAttribute {

  private final UUID uuid;
  private String name;
  private JSONObject json; // reward attributes

  public abstract void giveReward(Player player);

}
