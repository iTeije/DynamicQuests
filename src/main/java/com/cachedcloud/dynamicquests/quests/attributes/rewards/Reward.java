package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.BaseAttribute;
import lombok.Getter;
import me.lucko.helper.utils.Players;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.UUID;

@Getter
public abstract class Reward implements BaseAttribute {

  private final UUID uuid;
  private String name;
  private JSONObject json; // reward attributes

  public Reward(UUID uuid, String name, JSONObject json) {
    this.uuid = uuid;
    this.name = name;
    this.json = json;
  }

  public void sendRewardMessage(Player player) {
    // Send message to player in case there is one
    String msg = json.getString("message");

    if (msg != null) Players.msg(player, msg);
  }

  public abstract void giveReward(Player player);

}
