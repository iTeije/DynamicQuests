package com.cachedcloud.dynamicquests.quests;

import com.cachedcloud.dynamicquests.quests.gui.MainQuestGui;
import me.lucko.helper.Commands;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuestModule implements TerminableModule {

  // SQL statements
  private static final String CREATE_QUESTS_TABLE = "CREATE TABLE IF NOT EXISTS quests (" +
      "`uuid` varchar(36) NOT NULL, " +
      "`name` varchar(64) NOT NULL, " +
      "`description` TEXT NOT NULL)";
  private static final String GET_QUESTS = "SELECT * FROM `quests`";
  private static final String CREATE_QUEST = "INSERT INTO `quests` (`uuid`, `name`, `description`) VALUES (?, ?, ?)";
  private static final String UPDATE_QUEST = "UPDATE `quests` SET `name` = ?, `description` = ? WHERE `uuid` = ?";

  // List of all quests
  private final List<Quest> quests = new ArrayList<>();

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    // Query all quests from database
    // todo

    // Create main quests command
    Commands.create()
        .assertPlayer()
        .handler(cmd -> {
          // Open quest GUI
          new MainQuestGui(cmd.sender(), this).open();
        }).registerAndBind(consumer, "quest", "quests");
  }

  public List<Quest> getQuests() {
    return this.quests;
  }
}
