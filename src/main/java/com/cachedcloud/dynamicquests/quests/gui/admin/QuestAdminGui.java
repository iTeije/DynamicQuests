package com.cachedcloud.dynamicquests.quests.gui.admin;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.utils.GuiUtil;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class QuestAdminGui extends Gui {

  private final static MenuScheme ATTRIBUTES_SCHEME = new MenuScheme()
      .maskEmpty(1)
      .mask("001010100")
      .maskEmpty(2);
  private final static MenuScheme NAVIGATION_SCHEME = new MenuScheme()
      .maskEmpty(3)
      .mask("100000001");

  private final Quest quest;

  public QuestAdminGui(Player player, Quest quest) {
    super(player, 4, "Edit Quest");
    this.quest = quest;
  }

  @Override
  public void redraw() {
    // Add filler items
    GuiUtil.fill(this);

    // Create populator for attributes
    MenuPopulator attributesPopulator = ATTRIBUTES_SCHEME.newPopulator(this);

    // Add item that displays the name and description of the quest
    attributesPopulator.accept(ItemStackBuilder.of(Material.NAME_TAG)
        .name("&e&lQuest Visuals")
        .apply(builder -> {
          builder.lore("&7Name: " + this.quest.getName());
          builder.lore("&7Description:");
          for (String line : this.quest.getDescription()) {
            builder.lore("&7  " + line);
          }
        })
        .lore(
            "",
            "&a[LEFT-CLICK] &7to change name.",
            "&e[RIGHT-CLICK] &7to change description."
        )
        .build(() -> {
          // todo right click
        }, () -> {
          // todo left click
        })
    );

    // Add item that opens the objectives submenu
    attributesPopulator.accept(ItemStackBuilder.of(Material.BOOK)
        .name("&e&lObjectives")
        .lore(
            "&7Click to view, modify, or",
            "&7add objectives.",
            "&8&o" + quest.getObjectives().size() + " objective(s)"
        )
        .build(() -> {
          // todo open objectives menu
        })
    );

    // Add item that opens the rewards submenu
    attributesPopulator.accept(ItemStackBuilder.of(Material.COPPER_INGOT)
        .name("&e&lRewards")
        .lore(
            "&7Click to view, modify, or",
            "&7add rewards.",
            "&8&o" + quest.getRewards().size() + " reward(s)"
        )
        .build(() -> {
          // todo open rewards menu
        })
    );


    // Create populator for navigation/action items
    MenuPopulator navigationPopulator = NAVIGATION_SCHEME.newPopulator(this);

    // Add 'return to main menu' item
    navigationPopulator.accept(ItemStackBuilder.of(Material.ARROW)
        .name("&cReturn")
        .lore("&7Go back to the main menu")
        .build(this::close)
    );

    // Add item to delete the quest
    navigationPopulator.accept(ItemStackBuilder.of(Material.BARRIER)
        .name("&c&lDelete Quest")
        .lore("&7Click here to delete the",
            "&7quest. You will be asked",
            "&7for a confirmation."
        )
        .build(() -> {
          // todo open delete confirmation menu
        })
    );
  }
}
