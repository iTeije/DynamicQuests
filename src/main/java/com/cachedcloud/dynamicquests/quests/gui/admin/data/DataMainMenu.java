package com.cachedcloud.dynamicquests.quests.gui.admin.data;

import com.cachedcloud.dynamicquests.DynamicQuests;
import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.QuestModule;
import com.cachedcloud.dynamicquests.quests.attributes.BaseAttribute;
import com.cachedcloud.dynamicquests.quests.gui.ConfirmationGui;
import com.cachedcloud.dynamicquests.utils.GuiUtil;
import com.cachedcloud.dynamicquests.utils.StringPrompt;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.function.Function;

public class DataMainMenu extends Gui {

  private static final MenuScheme ITEMS_SCHEME = new MenuScheme()
      .masks("111111111",
          "111111111",
          "111111111")
      .maskEmpty(1);

  private static final MenuScheme NAVIGATION_SCHEME = new MenuScheme()
      .maskEmpty(4)
      .mask("100010000");

  private final QuestModule questModule;
  private final Quest quest;
  private final BaseAttribute attribute;

  public DataMainMenu(Player player, QuestModule questModule, Quest quest, BaseAttribute attribute) {
    super(player, 5, "&8Edit Attributes");
    this.questModule = questModule;
    this.attribute = attribute;
    this.quest = quest;
  }

  @Override
  public void redraw() {
    // Show all attributes
    MenuPopulator attributesPopulator = ITEMS_SCHEME.newPopulator(this);
    JSONObject jsonObject = this.attribute.getJson();
    for (String key : jsonObject.keySet()) {
      if (!attributesPopulator.hasSpace()) break;

      // Add item showing the attribute/json key
      attributesPopulator.accept(ItemStackBuilder.of(Material.NAME_TAG)
          .name("&e&l" + key)
          .lore(
              "&7Value: &a" + jsonObject.get(key).toString(),
              "",
              "&a[LEFT-CLICK] &7to change the value.",
              "&e[RIGHT-CLICK] &7to delete this attribute."
          )
          .build(() -> {
            // Create and open a confirmation menu to delete the attribute
            ConfirmationGui confirmationGui = new ConfirmationGui(getPlayer(), "&8Delete Objective?",
                accept -> {
                  // todo delete objective + sql + exception handling maybe
                  attribute.updateAttribute(key, null);

                  // Close gui
                  accept.close();
                }, Gui::close);
            confirmationGui.setFallbackGui(p -> this);
            Function<Player, Gui> currentFallback = getFallbackGui();
            setFallbackGui(null);
            close();
            confirmationGui.open();
            setFallbackGui(currentFallback);
          }, () -> {
            // Change the value (string prompt)

            // Close menu
            final Function<Player, Gui> fallback = getFallbackGui();
            setFallbackGui(null);
            close();
            setFallbackGui(fallback);

            // Start string prompt to change the name
            StringPrompt.startPrompt(DynamicQuests.getInstance(), getPlayer(), "&7Enter a new value for this attribute (\"exit\" to cancel):", input -> {
              // Check if input is valid
              if (input != null) {
                // todo properly update + sql
                attribute.updateAttribute(key, input);
              }

              // Re-open menu
              redraw();
              open();
            });
          })
      );
    }

    // Fill row with filler items
    Item filler = ItemStackBuilder.of(GuiUtil.getDefaultFiller()).build(null);
    for (int i = 27; i <= 36; i++) {
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
        .name("&e&lCreate Attribute")
        .lore("&7Click here to create a new",
            "&7attribute.")
        .build(() -> {
          // Create new attribute

          // Close menu
          final Function<Player, Gui> fallback = getFallbackGui();
          setFallbackGui(null);
          close();
          setFallbackGui(fallback);

          // Start string prompt to change the name
          StringPrompt.startPrompt(DynamicQuests.getInstance(), getPlayer(), "&7Enter new attribute key and value in the format \"key=value\". (\"exit\" to cancel):", input -> {
            // Check if input is valid
            if (input != null) {
              // todo properly update + sql
              String[] data = input.split("=");
              if (data.length == 2) {
                attribute.updateAttribute(data[0], data[1]);
              }
            }

            // Re-open menu
            redraw();
            open();
          });
        })
    );
  }
}
