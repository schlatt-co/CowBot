package io.github.jroy.cowbot;

import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.managers.base.ProxyModule;
import io.github.jroy.cowbot.managers.proxy.DatabaseManager;
import io.github.jroy.cowbot.managers.proxy.DiscordManager;
import io.github.jroy.cowbot.managers.proxy.PlayerConnectionManager;
import io.github.jroy.cowbot.managers.proxy.PluginMessageManager;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class ProxiedCow extends Plugin implements Listener {

  private static ProxiedCow instance;

  private Configuration configuration;

  private List<ProxyModule> loadedModules = new ArrayList<>();

  private DiscordManager discordManager;
  private DatabaseManager databaseManager;
  private PlayerConnectionManager playerConnectionManager;

  @Override
  public void onLoad() {
    log("Hello <3 -Trevor");
  }

  @Override
  public void onEnable() {
    log("Loading CowBot...");
    instance = this;

    log("Loading config...");
    if (!loadConfig()) {
      log("The config either couldn't be created or its values were invalid. Proxy is shutting down...");
      getProxy().stop();
      return;
    }

    loadedModules.add(discordManager = new DiscordManager(this, configuration));
    loadedModules.add(databaseManager = new DatabaseManager(this, discordManager));
    discordManager.setDatabaseManager(databaseManager);
    loadedModules.add(playerConnectionManager = new PlayerConnectionManager(this, databaseManager));
    databaseManager.setPlayerConnectionManager(playerConnectionManager);
    loadedModules.add(new PluginMessageManager(this, discordManager, databaseManager, playerConnectionManager));
  }

  @Override
  public void onDisable() {
    for (ProxyModule module : loadedModules) {
      module.onDisable();
    }
  }

  private void log(String message) {
    getLogger().info("[Proxy] " + message);
  }

  @SuppressWarnings("UnstableApiUsage")
  private boolean loadConfig() {
    try {
      if (!getDataFolder().exists()) {
        getDataFolder().mkdir();
      }
      File file = new File(getDataFolder(), "config.yml");
      if (!file.exists()) {
        file.createNewFile();
        try (InputStream in = getResourceAsStream("example_config.yml");
             OutputStream out = new FileOutputStream(file)) {
          ByteStreams.copy(in, out);
        }
      }
      configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

      return configuration.getString("discord-token") != null && !configuration.getString("discord-token").isEmpty() && configuration.getString("discord-token").length() > 2;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public static ProxiedCow getInstance() {
    return instance;
  }
}
