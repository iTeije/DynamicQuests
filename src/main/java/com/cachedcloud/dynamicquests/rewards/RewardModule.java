package com.cachedcloud.dynamicquests.rewards;

import com.cachedcloud.dynamicquests.quests.Quest;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class RewardModule implements TerminableModule {

  // SQL statements
  private static final String CREATE_REWARDS_TABLE = "CREATE TABLE IF NOT EXISTS rewards (" +
      "`uuid` varchar(36) NOT NULL, " +
      "`quest_uuid` varchar(36) NOT NULL, " +
      "`name` varchar(64) NOT NULL, " +
      "`type` varchar(32) NOT NULL, " +
      "`attributes` JSON NOT NULL)";
  private static final String GET_REWARDS = "SELECT * FROM `rewards` WHERE `quest_uuid` = ?";
  private static final String CREATE_REWARD = "INSERT INTO `rewards`(`uuid`, `quest_uuid`, `name`, `type`, `attributes`) VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE_REWARD = "UPDATE `rewards` SET `attributes` = ?, `name` = ? WHERE `uuid` = ?";

  // Constructor parameters
  private final Sql sql;

  // Factories
  private RewardFactory rewardFactory;

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    // Create reward factory instance
    this.rewardFactory = new RewardFactory();
  }

  /**
   * Get the rewards that correspond to the uuid of the quest
   *
   * @param quest the quest to look up
   * @return the list of {@link Reward} objects that correspond to the quest uuid
   */
  public Promise<List<Reward>> getRewards(Quest quest) {
    // Query the database asynchronously
    return sql.queryAsync(GET_REWARDS, preparedStatement -> {
      // Insert quest uuid
      preparedStatement.setString(1, quest.getUuid().toString());
    }, resultSet -> {
      // Loop through resultset and build the corresponding rewards
      List<Reward> rewards = new ArrayList<>();
      while (resultSet.next()) {
        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
        String name = resultSet.getString("name");
        String type = resultSet.getString("type");
        JSONObject attributes = new JSONObject(resultSet.getString("attributes"));

        // Build reward in the RewardFactory
        rewards.add(this.rewardFactory.getReward(type, uuid, name, attributes));
      }

      return rewards;
    }).thenApplyAsync(optional -> optional.orElseGet(ArrayList::new));
  }
}
