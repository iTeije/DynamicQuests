package com.cachedcloud.dynamicquests.quests.gui;

import com.cachedcloud.dynamicquests.quests.Quest;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class QuestActionGui extends Gui {

  private final static MenuScheme SCHEME = new MenuScheme()
      .maskEmpty(1)
      .mask("000010000")
      .maskEmpty(1);

  private final Quest quest;
  private final boolean accept;

  public QuestActionGui(Player player, Quest quest, boolean accept) {
    super(player, 3, accept ? "&8Accept Quest?" : "&8Cancel Quest?");
    this.quest = quest;
    this.accept = accept;
  }

  @Override
  public void redraw() {
    MenuPopulator populator = SCHEME.newPopulator(this);

    // Check if the menu should show the start item, or instead show the cancel item
    if (this.accept) {
      // Add accept/start quest item
      populator.accept(ItemStackBuilder.of(Material.LIME_WOOL)
          .name("&a&lStart Quest")
          .lore(
              "&7Click here if you want to",
              "&7start the " + quest.getName() + " quest!"
          )
          .build(() -> {
            // todo start quest
          })
      );
    } else {
      // Add item to cancel the quest
      populator.accept(ItemStackBuilder.of(Material.RED_WOOL)
          .name("&c&lCancel Quest")
          .lore(
              "&7Click here if you want to",
              "&ccancel &7the " + quest.getName() + " quest.",
              "",
              "&4&lNOTE! &7Your progress will be lost."
          ).build(() -> {
            // todo cancel
          })
      );
    }
  }
}
