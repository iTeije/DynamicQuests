package com.cachedcloud.dynamicquests;

import com.cachedcloud.dynamicquests.messaging.MessageModule;
import com.cachedcloud.dynamicquests.quests.QuestModule;
import com.cachedcloud.dynamicquests.quests.attributes.objectives.ObjectiveModule;
import com.cachedcloud.dynamicquests.quests.attributes.rewards.RewardModule;
import com.cachedcloud.dynamicquests.quests.visuals.VisualsModule;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.sql.Sql;
import org.bukkit.configuration.file.FileConfiguration;

@Plugin(name = "DynamicQuests", version = "1.0-SNAPSHOT", hardDepends = {"helper", "helper-sql", "ProtocolLib"}, apiVersion = "1.20")
public final class DynamicQuests extends ExtendedJavaPlugin {

    @Getter
    private static org.bukkit.plugin.Plugin instance;

    @Override
    protected void enable() {
        /* Only use the instance when absolutely necessary (i.e. StringPrompts where it is
        often really inconvenient to deal with injecting the plugin as dependency)
         */
        instance = this;

        // Load resources and I/O services
        FileConfiguration config = loadConfig("config.yml");
        Sql sql = getService(Sql.class);

        MessageModule messageModule = bindModule(new MessageModule(config));

        RewardModule rewardModule = bindModule(new RewardModule(sql));
        ObjectiveModule objectiveModule = bindModule(new ObjectiveModule(sql));
        QuestModule questModule = bindModule(new QuestModule(sql, messageModule, rewardModule, objectiveModule));
        VisualsModule visualsModule = bindModule(new VisualsModule(config));
    }
}
