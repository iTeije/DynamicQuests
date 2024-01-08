package com.cachedcloud.dynamicquests.utils;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiUtil {

  private static final ItemStack FILLER = ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();

  public static void fill(Gui gui) {
    fill(gui, FILLER);
  }

  public static void fill(Gui gui, ItemStack stack) {
    Item item = ItemStackBuilder.of(stack).build(null);
    for (int i = 0; i < gui.getHandle().getSize(); i++) {
      gui.setItem(i, item);
    }
  }

  public static ItemStack getDefaultFiller() {
    return FILLER;
  }

}
