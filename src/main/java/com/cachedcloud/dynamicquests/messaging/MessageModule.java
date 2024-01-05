package com.cachedcloud.dynamicquests.messaging;

import lombok.RequiredArgsConstructor;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MessageModule implements TerminableModule {

  private final FileConfiguration config;
  private final Map<String, String> messages = new HashMap<>();

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    // To save on unnecessary I/O overhead, I'll be caching all messages
    ConfigurationSection section = config.getConfigurationSection("messages");
    for (String key : section.getKeys(true)) {
      this.messages.put(key, section.getString(key));
    }
  }

  /**
   * Get a formatted message from the config
   *
   * @param key the key that specifies the path of the message in the config file
   * @param params custom parameters of the message
   * @return the formatted message
   */
  public String getAndFormat(StorageKey key, String... params) {
    return String.format(
        this.messages.getOrDefault(key.getPath(), "An error occurred while sending message (" + key.getPath() + ")"),
        (Object[]) params
    );
  }
}
