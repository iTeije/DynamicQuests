package com.cachedcloud.dynamicquests;

import com.cachedcloud.dynamicquests.messaging.MessageModule;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.ObjectiveModule;
import com.cachedcloud.dynamicquests.quests.QuestModule;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.RewardModule;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.sql.Sql;
import org.bukkit.configuration.file.FileConfiguration;

@Plugin(name = "DynamicQuests", version = "1.0-SNAPSHOT", hardDepends = {"helper", "helper-sql"}, apiVersion = "1.20")
public final class DynamicQuests extends ExtendedJavaPlugin {

    @Override
    protected void enable() {
        // Load resources and I/O services
        FileConfiguration config = loadConfig("config.yml");
        Sql sql = getService(Sql.class);

        MessageModule messageModule = bindModule(new MessageModule(config));

        RewardModule rewardModule = bindModule(new RewardModule(sql));
        ObjectiveModule objectiveModule = bindModule(new ObjectiveModule(sql));
        QuestModule questModule = bindModule(new QuestModule(sql, messageModule, rewardModule, objectiveModule));
    }
}
