package com.cachedcloud.dynamicquests.quests.gui.admin;

import com.cachedcloud.dynamicquests.DynamicQuests;
import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.QuestModule;
import com.cachedcloud.dynamicquests.quests.gui.admin.attributes.QuestObjectivesAdminGui;
import com.cachedcloud.dynamicquests.quests.gui.admin.attributes.QuestRewardsAdminGui;
import com.cachedcloud.dynamicquests.utils.GuiUtil;
import com.cachedcloud.dynamicquests.utils.StringPrompt;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.function.Function;

public class QuestAdminGui extends Gui {

  private final static MenuScheme ATTRIBUTES_SCHEME = new MenuScheme()
      .maskEmpty(1)
      .mask("001010100")
      .maskEmpty(2);
  private final static MenuScheme NAVIGATION_SCHEME = new MenuScheme()
      .maskEmpty(3)
      .mask("100000001");

  private final Quest quest;
  private final QuestModule questModule;

  public QuestAdminGui(Player player, Quest quest, QuestModule questModule) {
    super(player, 4, "&8Edit Quest");
    this.quest = quest;
    this.questModule = questModule;
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
          // Close menu
          final Function<Player, Gui> fallback = getFallbackGui();
          setFallbackGui(null);
          close();
          setFallbackGui(fallback);

          // Start string prompt to change the name
          StringPrompt.startPrompt(DynamicQuests.getInstance(), getPlayer(), "&7Enter new quest description. Use \"\\n\" to add a line break. (\"exit\" to cancel):", input -> {
            // Check if input is valid
            if (input != null) {
              // todo properly update + sql
              // Set description and split it on a literal "\n"
              quest.setDescription(Arrays.asList(input.split("\\\\n")));
            }

            // Re-open menu
            redraw();
            open();
          });
        }, () -> {
          // Close menu
          final Function<Player, Gui> fallback = getFallbackGui();
          setFallbackGui(null);
          close();
          setFallbackGui(fallback);

          // Start string prompt to change the name
          StringPrompt.startPrompt(DynamicQuests.getInstance(), getPlayer(), "&7Enter new quest name (\"exit\" to cancel):", input -> {
            // Check if input is valid
            if (input != null) {
              // todo properly update + sql
              quest.setName(input);
            }

            // Re-open menu
            redraw();
            open();
          });
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
          // Open objectives gui
          QuestObjectivesAdminGui objectivesGui = new QuestObjectivesAdminGui(getPlayer(), quest, questModule);
          objectivesGui.setFallbackGui(p -> this);

          Function<Player, Gui> currentFallback = getFallbackGui();
          setFallbackGui(null);

          close();
          objectivesGui.open();
          setFallbackGui(currentFallback);
        })
    );

    // Add item that opens the rewards submenu
    attributesPopulator.accept(ItemStackBuilder.of(Material.GOLD_INGOT)
        .name("&e&lRewards")
        .lore(
            "&7Click to view, modify, or",
            "&7add rewards.",
            "&8&o" + quest.getRewards().size() + " reward(s)"
        )
        .build(() -> {
          // Open rewards gui
          QuestRewardsAdminGui rewardsGui = new QuestRewardsAdminGui(getPlayer(), quest, questModule);
          rewardsGui.setFallbackGui(p -> this);

          Function<Player, Gui> currentFallback = getFallbackGui();
          setFallbackGui(null);

          close();
          rewardsGui.open();
          setFallbackGui(currentFallback);
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
