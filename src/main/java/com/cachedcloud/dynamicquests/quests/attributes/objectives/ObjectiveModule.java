package com.cachedcloud.dynamicquests.quests.attributes.objectives;

import com.cachedcloud.dynamicquests.quests.Quest;
import com.cachedcloud.dynamicquests.quests.attributes.BaseAttributeModule;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ObjectiveModule extends BaseAttributeModule<Objective> {

  public ObjectiveModule(Sql sql) {
    super(sql, "objectives", new ObjectiveFactory());
  }

  private TerminableConsumer consumer;

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    super.setup(consumer);
    this.consumer = CompositeTerminable.create();
  }

  @Override
  public void applyAttributes(Quest quest, List<Objective> attributes) {
    quest.getObjectives().addAll(attributes);

    // Register listeners for each objective
    attributes.forEach(this::handleNew);
  }

  @Override
  public void handleNew(Objective attribute) {
    attribute.registerListeners(consumer);
  }
}