package com.cachedcloud.dynamicquests.quests.gui;

import com.cachedcloud.dynamicquests.messaging.StorageKey;
import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.tracking.ProgressModule;
import com.cachedcloud.dynamicquests.utils.GuiUtil;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.utils.Players;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class QuestActionGui extends Gui {

  private final static MenuScheme SCHEME = new MenuScheme()
      .maskEmpty(1)
      .mask("000010000")
      .maskEmpty(1);

  private final Quest quest;
  private final ProgressModule progressModule;
  private final boolean accept;

  public QuestActionGui(Player player, ProgressModule progressModule, Quest quest, boolean accept) {
    super(player, 3, accept ? "&8Accept Quest?" : "&8Cancel Quest?");
    this.quest = quest;
    this.accept = accept;
    this.progressModule = progressModule;
  }

  @Override
  public void redraw() {
    // Fill gui with filler items
    GuiUtil.fill(this);

    MenuPopulator populator = SCHEME.newPopulator(this);

    // Check if the menu should show the start item, or instead show the cancel item
    if (this.accept) {
      // Add accept/start quest item
      populator.accept(ItemStackBuilder.of(Material.LIME_WOOL)
          .name("&a&lStart Quest")
          .lore(
              "&7Click here if you want to",
              "&7start the " + quest.getName() + " &7quest!"
          )
          .build(() -> {
            // Start the quest
            this.progressModule.startTracking(getPlayer(), this.quest);

            // Send confirmation message
            Players.msg(
                getPlayer(),
                this.progressModule.getMessageModule().getAndFormat(StorageKey.MENU_QUEST_START_SUCCESS, quest.getName())
            );

            // Close all menus
            setFallbackGui(null);
            close();
          })
      );

    } else {
      // Add item to cancel the quest
      populator.accept(ItemStackBuilder.of(Material.RED_WOOL)
          .name("&c&lCancel Quest")
          .lore(
              "&7Click here if you want to &ccancel",
              "&7the " + quest.getName() + " &7quest.",
              "",
              "&4&lNOTE! &7Your progress will be lost."
          ).build(() -> {
            // Cancel the quest
            this.progressModule.deleteProgress(getPlayer().getUniqueId(), quest);

            // Send confirmation message of the cancelled quest
            Players.msg(
                getPlayer(),
                this.progressModule.getMessageModule().getAndFormat(StorageKey.MENU_QUEST_CANCEL_SUCCESS, quest.getName())
            );

            // Close all menus
            setFallbackGui(null);
            close();
          })
      );
    }
  }
}
