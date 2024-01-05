package com.cachedcloud.dynamicquests;

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

        
    }
}
