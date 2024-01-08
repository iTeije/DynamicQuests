package com.cachedcloud.dynamicquests.quests.gui;

import com.cachedcloud.dynamicquests.messaging.StorageKey;
import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.QuestModule;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.Reward;
import com.cachedcloud.dynamicquests.quests.tracking.ProgressModule;
import com.cachedcloud.dynamicquests.quests.tracking.QuestProgress;
import com.cachedcloud.dynamicquests.utils.GuiUtil;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.paginated.PaginatedGui;
import me.lucko.helper.menu.paginated.PaginatedGuiBuilder;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.utils.Players;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MainQuestGui extends PaginatedGui {

  private static final MenuScheme SCHEME = new MenuScheme()
      .maskEmpty(5);

  public MainQuestGui(Player player, ProgressModule progressModule, QuestModule questModule) {
    super(
        paginatedGui -> buildQuestItems(paginatedGui, player, progressModule, questModule),
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
    // Fill gui with filler items
    GuiUtil.fill(this);

    // Handle pagination
    super.redraw();
  }

  private static List<Item> buildQuestItems(final PaginatedGui paginatedGui, Player player, final ProgressModule progressModule, final QuestModule questModule) {
    // Create a collection of quests
    Collection<Quest> quests = questModule.getValidQuests();

    // Get active Quest
    QuestProgress progress = progressModule.getCurrentQuestProgress(player.getUniqueId());
    Quest activeQuest = progress != null ? progress.getQuest() : null;
    boolean appendProgress = activeQuest != null;

    // Create a menu item for all quests
    return quests.stream().map(quest -> {
      return ItemStackBuilder.of(Material.PAPER)
          .name(quest.getName())
          .lore(quest.getDescription())
          .apply(builder -> {
            // Add objectives section
            builder.lore("", "&eObjectives");
            for (Objective objective : quest.getObjectives()) {
              // If the player is currently doing this quest, append the progress of each objective
              builder.lore("&7- " + objective.getName() +
                  (appendProgress ? "&7: &a" + progressModule.getObjectiveProgressPercentage(progress, objective) + "%" : "")
              );
            }
            if (quest.getObjectives().size() == 0) {
              builder.lore("&7None");
            }

            // Add rewards section
            builder.lore("", "&eRewards");
            for (Reward reward : quest.getRewards()) {
              builder.lore("&7- " + reward.getName());
            }
            if (quest.getRewards().size() == 0) {
              builder.lore("&7None");
            }

            // Enchant if this quest is active
            if (quest.equals(activeQuest)) {
              builder.enchant(Enchantment.ARROW_FIRE, 1);
              builder.flag(ItemFlag.HIDE_ENCHANTS);
            }
          })
          .build(() -> {
            // If the player does not have an active quest, OR this quest is the active quest,
            // allow the player to open the quest action GUI
            if (activeQuest == null || quest.equals(activeQuest)) {
              Gui actionGui = new QuestActionGui(player, progressModule, quest, !quest.equals(activeQuest));
              actionGui.setFallbackGui(p -> paginatedGui);
              paginatedGui.close();
              actionGui.open();

            } else {
              // Send message indicating that the player is already doing a quest
              Players.msg(player, questModule.getMessageModule().getAndFormat(StorageKey.MENU_ACTIVE_QUEST_ERROR));
              paginatedGui.close();
            }
          });
    }).collect(Collectors.toList());
  }
}
