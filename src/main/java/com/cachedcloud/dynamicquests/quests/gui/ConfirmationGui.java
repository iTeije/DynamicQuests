package com.cachedcloud.dynamicquests.quests.gui;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ConfirmationGui extends Gui {

  private final static MenuScheme SCHEME = new MenuScheme()
      .maskEmpty(1)
      .mask("001000100")
      .maskEmpty(1);

  private final String acceptName;
  private final String[] acceptLore;
  private final Consumer<Gui> acceptConsumer;

  private final String declineName;
  private final String[] declineLore;
  private final Consumer<Gui> declineConsumer;

  private boolean alreadyChose = false;

  public ConfirmationGui(Player player, String title, Consumer<Gui> acceptConsumer, Consumer<Gui> declineConsumer) {
    this(player, title, "&aClick here to confirm", new String[0], acceptConsumer,
        "&cClick here to cancel", new String[0], declineConsumer);
  }

  public ConfirmationGui(Player player, String title,
                         String acceptName, String[] acceptLore, Consumer<Gui> acceptConsumer,
                         String declineName, String[] declineLore, Consumer<Gui> declineConsumer) {
    super(player, 3, title);
    this.acceptName = acceptName;
    this.acceptLore = acceptLore;
    this.acceptConsumer = acceptConsumer;
    this.declineName = declineName;
    this.declineLore = declineLore;
    this.declineConsumer = declineConsumer;
  }

  @Override
  public void redraw() {
    MenuPopulator populator = SCHEME.newPopulator(this);
    // Add decline item
    populator.accept(ItemStackBuilder.of(Material.RED_STAINED_GLASS_PANE)
        .name(declineName)
        .lore(declineLore)
        .build(() -> {
          if (alreadyChose) {
            return;
          }
          alreadyChose = true;
          declineConsumer.accept(this);
        }));

    // Add accept item
    populator.accept(ItemStackBuilder.of(Material.LIME_STAINED_GLASS_PANE)
        .name(acceptName)
        .lore(acceptLore)
        .build(() -> {
          if (alreadyChose) {
            return;
          }
          alreadyChose = true;
          acceptConsumer.accept(this);
        }));
  }
}
