package com.cachedcloud.dynamicquests.quests.gui;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.QuestModule;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.paginated.PaginatedGui;
import me.lucko.helper.menu.paginated.PaginatedGuiBuilder;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.utils.Players;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainQuestGui extends PaginatedGui {

  private static final MenuScheme SCHEME = new MenuScheme()
      .maskEmpty(5);

  public MainQuestGui(Player player, QuestModule questModule) {
    super(
        paginatedGui -> buildQuestItems(paginatedGui, player, questModule.getQuests()),
        player,
        PaginatedGuiBuilder.create()
            .title("&8&lQuests")
            .lines(5)
            .itemSlots(Arrays.asList(11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33))
            .previousPageSlot(28)
            .scheme(SCHEME)
            .previousPageItem(
                pageInfo -> ItemStackBuilder.of(Material.ARROW)
                    .name("&c&lPrevious Page")
                    .lore("&8&oPage " + pageInfo.getCurrent() + "/" + pageInfo.getSize())
                    .build())
            .nextPageSlot(34)
            .nextPageItem(
                pageInfo -> ItemStackBuilder.of(Material.ARROW)
                    .name("&c&lNext Page")
                    .lore("&8&oPage " + pageInfo.getCurrent() + "/" + pageInfo.getSize())
                    .build())
        );
  }

  @Override
  public void redraw() {
    super.redraw();
  }

  private static List<Item> buildQuestItems(final PaginatedGui paginatedGui, Player player, final List<Quest> quests) {
    return quests.stream().map(quest -> {
      return ItemStackBuilder.of(Material.PAPER)
          .name(quest.getName())
          .lore(quest.getDescription())
          .transformMeta(meta -> {
            // enchant if quest is active todo
          })
          .build(() -> {
            // todo start/cancel
            Players.msg(player, "boop");
          });
    }).collect(Collectors.toList());
  }
}
