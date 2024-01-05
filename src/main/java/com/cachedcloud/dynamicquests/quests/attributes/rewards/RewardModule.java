package com.cachedcloud.dynamicquests.quests.attributes.rewards;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.attributes.BaseAttributeModule;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.terminable.TerminableConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RewardModule extends BaseAttributeModule<Reward> {

  public RewardModule(Sql sql) {
    super(sql, "rewards", new RewardFactory());
  }

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    super.setup(consumer);
    // maybe handle stuff in the future
  }

  @Override
  public void applyAttributes(Quest quest, List<Reward> attributes) {
    // Apply rewards
    quest.getRewards().addAll(attributes);
  }
}
