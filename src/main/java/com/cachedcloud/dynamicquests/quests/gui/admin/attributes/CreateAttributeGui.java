package com.cachedcloud.dynamicquests.quests.gui.admin.attributes;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.QuestModule;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.Objective;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.ObjectiveType;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.Reward;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.RewardType;
import com.cachedcloud.dynamicquests.utils.GuiUtil;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CreateAttributeGui extends Gui {

  private static final MenuScheme ITEMS_SCHEME = new MenuScheme()
      .masks("111111111",
          "111111111",
          "111111111")
      .maskEmpty(1);

  private static final MenuScheme NAVIGATION_SCHEME = new MenuScheme()
      .maskEmpty(4)
      .mask("100010000");

  private final Quest quest;
  private final QuestModule questModule;
  private final boolean isObjective;

  public CreateAttributeGui(Player player, Quest quest, QuestModule questModule, boolean isObjective) {
    super(player, 5, "&8Create Objective");
    this.quest = quest;
    this.questModule = questModule;
    this.isObjective = isObjective;
  }

  @Override
  public void redraw() {
    // Place all different attribute types
    MenuPopulator typesPopulator = ITEMS_SCHEME.newPopulator(this);

    if (isObjective) {
      // Fill objective types
      for (ObjectiveType type : ObjectiveType.values()) {
        if (!typesPopulator.hasSpace()) break;

        typesPopulator.accept(ItemStackBuilder.of(Material.PAPER)
            .name("&e&l" + type.name())
            .lore("&7Click to create an objective", "&7with this type.")
            .build(() -> {
              // Create a new objective
              Objective newObjective = type.createDefault();

              // Add objective to quest
              quest.getObjectives().add(newObjective);

              // Update database
              questModule.getObjectiveModule().createAttribute(quest, newObjective);

              close();
            })
        );
      }
    } else {
      // Fill reward types
      for (RewardType type : RewardType.values()) {
        if (!typesPopulator.hasSpace()) break;

        typesPopulator.accept(ItemStackBuilder.of(Material.GOLD_INGOT)
            .name("&e&l" + type.name())
            .lore("&7Click to create a reward", "&7of this type.")
            .build(() -> {
              // Create a new reward
              Reward newReward = type.createDefault();

              // Add reward to quest
              quest.getRewards().add(newReward);

              // Update database
              questModule.getRewardModule().createAttribute(quest, newReward);

              close();
            })
        );
      }
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
  }
}
