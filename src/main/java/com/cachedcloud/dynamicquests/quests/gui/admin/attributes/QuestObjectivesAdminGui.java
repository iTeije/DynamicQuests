package com.cachedcloud.dynamicquests.quests.gui.admin.attributes;

import com.cachedcloud.dynamicquests.DynamicQuests;
import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.QuestModule;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import com.cachedcloud.dynamicquests.quests.gui.ConfirmationGui;
import com.cachedcloud.dynamicquests.quests.gui.admin.data.DataMainMenu;
import com.cachedcloud.dynamicquests.utils.GuiUtil;
import com.cachedcloud.dynamicquests.utils.StringPrompt;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.function.Function;

public class QuestObjectivesAdminGui extends Gui {

  private static final MenuScheme ITEMS_SCHEME = new MenuScheme()
      .masks("111111111",
          "111111111",
          "111111111",
          "111111111")
      .maskEmpty(2);
  private static final MenuScheme NAVIGATION_SCHEME = new MenuScheme()
      .maskEmpty(5)
      .mask("100010000");

  private final Quest quest;
  private final QuestModule questModule;

  public QuestObjectivesAdminGui(Player player, Quest quest, QuestModule questModule) {
    super(player, 6, "&8Edit Objectives");
    this.quest = quest;
    this.questModule = questModule;
  }

  @Override
  public void redraw() {
    // Show all objectives
    MenuPopulator populator = ITEMS_SCHEME.newPopulator(this);
    for (Objective objective : quest.getObjectives()) {
      // Make sure the populator has space to prevent errors
      if (!populator.hasSpace()) break;

      // Place item
      populator.accept(ItemStackBuilder.of(Material.PAPER)
          .name("&e&lObjective")
          .apply(builder -> {
            builder.lore(
                "&7Name: &a" + objective.getName(),
                "&7Type: &a" + objective.getClass().getSimpleName(),
                "&7Attributes:"
            );
            // Show the json data of the objective
            for (String key : objective.getJson().keySet()) {
              builder.lore("  &7" + key + ": &7&o" + objective.getJson().get(key));
            }
            builder.lore(
                "",
                "&a[LEFT-CLICK] &7to change the attributes.",
                "&e[RIGHT-CLICK] &7to change the name.",
                "&c[MIDDLE-CLICK] &7to delete the objective."
            );
          })
          .buildItem()
          .bind(ClickType.RIGHT, () -> { // change name
            // Close menu
            final Function<Player, Gui> fallback = getFallbackGui();
            setFallbackGui(null);
            close();
            setFallbackGui(fallback);

            // Start string prompt to change the name
            StringPrompt.startPrompt(DynamicQuests.getInstance(), getPlayer(), "&7Enter new objective name (\"exit\" to cancel):", input -> {
              // Check if input is valid
              if (input != null) {
                // todo properly update + sql
                objective.setName(input);
              }

              // Re-open menu
              redraw();
              open();
            });
          })
          .bind(ClickType.LEFT, () -> { // change attributes (open menu)
            // Open the attributes menu
            DataMainMenu dataMainMenu = new DataMainMenu(getPlayer(), questModule, quest, objective);
            dataMainMenu.setFallbackGui(p -> this);
            Function<Player, Gui> currentFallback = getFallbackGui();
            setFallbackGui(null);
            close();
            dataMainMenu.open();
            setFallbackGui(currentFallback);
          })
          .bind(ClickType.MIDDLE, () -> { // delete objective
            // Create and open a confirmation menu
            ConfirmationGui confirmationGui = new ConfirmationGui(getPlayer(), "&8Delete Objective?",
                accept -> {
                  // todo delete objective + sql
                  quest.getObjectives().remove(objective);

                  // Close gui
                  accept.close();
                }, Gui::close);
            confirmationGui.setFallbackGui(p -> this);
            close();
            confirmationGui.open();
          })
          .build()
      );
    }

    // Fill row with filler items
    Item filler = ItemStackBuilder.of(GuiUtil.getDefaultFiller()).build(null);
    for (int i = 36; i <= 45; i++) {
      setItem(i, filler);
    }

    // Add navigation buttons
    MenuPopulator navigationPopulator = NAVIGATION_SCHEME.newPopulator(this);
    // Return button
    navigationPopulator.accept(ItemStackBuilder.of(Material.ARROW)
        .name("&cReturn")
        .lore("&7Go back to the quest menu")
        .build(this::close)
    );

    // Create new objective button
    navigationPopulator.accept(ItemStackBuilder.of(Material.WRITABLE_BOOK)
        .name("&e&lCreate Objective")
        .lore("&7Click here to create a new",
            "&7objective.")
        .build(() -> {
          // todo open menu to select type (ObjectiveType)
        })
    );
  }
}
