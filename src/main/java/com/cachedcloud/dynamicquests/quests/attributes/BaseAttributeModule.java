package com.cachedcloud.dynamicquests.quests.attributes;

import com.cachedcloud.dynamicquests.quests.Quest;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseAttributeModule<T extends BaseAttribute> implements TerminableModule {

  // SQL statements
  private final String createAttributeTableStatement;
  private final String getAttributesStatement;
  private final String createAttributeStatement;
  private final String updateAttributeStatement;

  private final Factory<T> factory;
  private final Sql sql;

  public BaseAttributeModule(Sql sql, String attributeName, Factory<T> factory) {
    this.sql = sql;
    this.factory = factory;

    createAttributeTableStatement = "CREATE TABLE IF NOT EXISTS " + attributeName + " (" +
        "`uuid` varchar(36) NOT NULL, " +
        "`quest_uuid` varchar(36) NOT NULL, " +
        "`name` varchar(64) NOT NULL, " +
        "`type` varchar(32) NOT NULL, " +
        "`attributes` JSON NOT NULL)";
    getAttributesStatement = "SELECT * FROM `" + attributeName + "` WHERE `quest_uuid` = ?";
    createAttributeStatement = "INSERT INTO `" + attributeName + "`(`uuid`, `quest_uuid`, `name`, `type`, `attributes`) VALUES (?, ?, ?, ?, ?)";
    updateAttributeStatement = "UPDATE `" + attributeName + "` SET `attributes` = ?, `name` = ? WHERE `uuid` = ?";
  }

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    // Create table
    sql.executeAsync(createAttributeTableStatement);
  }

  /**
   * Get and apply the attribute to the quest
   *
   * @param quest the quest to load the attributes for (one to many)
   */
  public void loadAttribute(Quest quest) {
    // Query the database - ONLY CALL THIS METHOD ASYNC
    Optional<List<T>> attributeOptional = sql.query(getAttributesStatement, preparedStatement -> {
      // Insert quest uuid
      preparedStatement.setString(1, quest.getUuid().toString());
    }, resultSet -> {
      // Loop through resultset and build the corresponding attributes
      List<T> attributes = new ArrayList<>();
      while (resultSet.next()) {
        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
        String name = resultSet.getString("name");
        String type = resultSet.getString("type");
        JSONObject json = new JSONObject(resultSet.getString("attributes"));

        // Build reward in the RewardFactory
        attributes.add(this.factory.getAttribute(type, uuid, name, json));
      }

      return attributes;
    });

    // Call to abstract apply method
    applyAttributes(quest, attributeOptional.orElse(new ArrayList<>()));
  }

  public abstract void applyAttributes(Quest quest, List<T> attributes);
}
