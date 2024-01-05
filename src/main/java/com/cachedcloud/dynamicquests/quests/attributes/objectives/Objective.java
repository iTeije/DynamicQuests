package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.attributes.BaseAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public abstract class Objective implements BaseAttribute {

  private final UUID uuid;
  private String name;
  private JSONObject json; // reward attributes

  public abstract void trackPlayer(Player player);

}
