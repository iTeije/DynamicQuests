package com.cachedcloud.dynamicquests.quests.gui.admin;

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

public class QuestAdminGui extends PaginatedGui {

  private static final MenuScheme SCHEME = new MenuScheme()
      .maskEmpty(5);

  public QuestAdminGui(Player player, QuestModule questModule) {
    super(
        paginatedGui -> buildQuestItems(player, questModule.getQuests()),
        player,
        PaginatedGuiBuilder.create()
            .title("&8Quest Management")
            .lines(5)
            .itemSlots(Arrays.asList(11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33)) // item slots that display the quest items
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

    // Add button to create new quest
    setItem(40, ItemStackBuilder.of(Material.WRITABLE_BOOK)
        .name("&e&lCreate Quest")
        .lore("&7Click this item to start", "&7the setup of creating", "&7a new quest.")
        .build(() -> {

        })
    );
  }

  private static List<Item> buildQuestItems(Player player, final List<Quest> quests) {
    // Create the admin version of the quest menu items
    return quests.stream().map(quest -> {
      return ItemStackBuilder.of(Material.PAPER)
          .name(quest.getName())
          .lore(quest.getDescription())
          .lore(
              "",
              "&7Rewards: &a" + quest.getRewards().size(),
              "&7Objectives: &a" + quest.getObjectives().size(),
              "",
              "&7Click to update quest &c(admin)"
          )
          .build(() -> {
            // todo open submenu
            Players.msg(player, "open submenu");
          });
    }).collect(Collectors.toList());
  }

}
