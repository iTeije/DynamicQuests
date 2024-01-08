package com.cachedcloud.dynamicquests.quests.attributes.rewards.types;

import com.cachedcloud.dynamicquests.quests.attributes.rewards.Reward;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.NoSuchElementException;
import java.util.UUID;

public class ConsoleCommandReward extends Reward {

  private String command;

  public ConsoleCommandReward(UUID uuid, String name, JSONObject json) {
    super(uuid, name, json, RewardType.CONSOLE_COMMAND);
  }

  @Override
  public void giveReward(Player player) {
    // Send reward message
    super.sendRewardMessage(player);

    // Format command (1st arg = player name, 2nd arg = player uuid)
    String formattedCommand = String.format(command, player.getName(), player.getUniqueId());

    // Dispatch the command as console
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
  }

  @Override
  public void parseJson(JSONObject json) throws JSONException, NullPointerException, NoSuchElementException {
    this.command = json.getString("command");
  }
}
