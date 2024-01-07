package com.cachedcloud.dynamicquests.quests.attributes.rewards.types;

import com.cachedcloud.dynamicquests.quests.attributes.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.UUID;

public class ConsoleCommandReward extends Reward {

  private final String command;

  public ConsoleCommandReward(UUID uuid, String name, JSONObject json) {
    super(uuid, name, json);
    this.command = json.getString("command");
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
}
