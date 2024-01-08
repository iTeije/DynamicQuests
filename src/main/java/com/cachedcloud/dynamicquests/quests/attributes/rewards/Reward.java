package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.attributes.BaseAttribute;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.utils.Players;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.UUID;

@Getter
public abstract class Reward implements BaseAttribute {

  private final UUID uuid;
  private final JSONObject json; // reward attributes

  @Setter private String name;

  public Reward(UUID uuid, String name, JSONObject json) {
    this.uuid = uuid;
    this.name = name;
    this.json = json;
  }

  public void sendRewardMessage(Player player) {
    // Send message to player in case there is one
    String msg = json.getString("completion_message");

    if (msg != null) Players.msg(player, msg);
  }

  public abstract void giveReward(Player player);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Reward reward = (Reward) o;

    return uuid.equals(reward.uuid);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }
}
