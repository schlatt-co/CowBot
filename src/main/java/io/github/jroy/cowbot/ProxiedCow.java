package io.github.jroy.cowbot;

import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.commands.*;
import io.github.jroy.cowbot.commands.base.CommandFactory;
import io.github.jroy.cowbot.utils.DatabaseFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProxiedCow extends Plugin implements Listener {

  public static ProxiedCow instance;
  private Configuration configuration;
  public DatabaseFactory databaseFactory;

  public boolean isLockdown = false;
  public List<String> lockdownList = new ArrayList<>();

  @Override
  public void onEnable() {
    instance = this;
    lockdownList.add("WheezyGold7931");
    lockdownList.add("wolfmitchell");
    getLogger().info("[CowBot] [Proxy] onEnable - pre");
    if (loadConfig()) {
      CommandFactory commandFactory = new CommandFactory("!", ".");
      commandFactory.addCommands(new LinkCommand(this), new BanCommand(this), new ViveCommand(this));

      getLogger().info("[CowBot] [Proxy] Logging into JDA...");
      JDA jda;
      try {
        jda = new JDABuilder(AccountType.BOT)
            .setToken(configuration.getString("discord-token"))
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .addEventListener(commandFactory.build())
            .build();
      } catch (LoginException e) {
        getLogger().info("[CowBot] [Proxy] JDA failed to login, shutting down.");
        return;
      }

      try {
        databaseFactory = new DatabaseFactory(jda, getDataFolder());
      } catch (ClassNotFoundException | SQLException e) {
        getLogger().info("[CowBot] [Proxy] Unable to connect to the database, aborting...");
        return;
      }

      getProxy().getPluginManager().registerListener(this, this);
      getProxy().getPluginManager().registerCommand(this, new TrevorCommand(this, jda));
      getProxy().getPluginManager().registerCommand(this, new LockdownCommand(this));
      getProxy().getPluginManager().registerCommand(this, new StopCommand());
    }
  }

  @EventHandler
  public void onPostLoginEvent(LoginEvent event) {
    if (isLockdown && !lockdownList.contains(event.getConnection().getName())) {
      event.setCancelReason(new TextComponent("Server is in lockdown while we preform some upgrades ;)"));
      event.setCancelled(true);
      return;
    }

    if (!databaseFactory.isWhitelisted(event.getConnection().getName())) {
      event.setCancelReason(new TextComponent("You are not whitelisted!\nGive Schlatt Fucking Money\nThen do \"!link " + event.getConnection().getName() + "\" in the #mc channel on the discord server"));
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onServerConnectEvent(ServerConnectEvent event) {
    if (event.getPlayer().getPendingConnection().getVersion() <= 479) {
      if (databaseFactory.isVive(event.getPlayer().getName())) {
        event.setTarget(getProxy().getServerInfo("vivecraft"));
      } else {
        event.getPlayer().disconnect(new TextComponent("Hey Troglodyte,\nWe updated the server to 1.14.1!\nSo you can't join with whatever shitty version you're on."));
      }
      return;
    }
    event.setTarget(getProxy().getServerInfo("vanilla"));
  }

  @SuppressWarnings({"ResultOfMethodCallIgnored", "UnstableApiUsage"})
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
}
