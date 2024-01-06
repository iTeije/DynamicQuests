package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.attributes.BaseAttributeModule;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.terminable.TerminableConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ObjectiveModule extends BaseAttributeModule<Objective> {

  public ObjectiveModule(Sql sql) {
    super(sql, "objectives", new ObjectiveFactory());
  }

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    super.setup(consumer);
  }

  @Override
  public void applyAttributes(Quest quest, List<Objective> attributes) {
    quest.getObjectives().addAll(attributes);
  }
}