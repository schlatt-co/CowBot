package io.github.jroy.cowbot;

import io.github.jroy.cowbot.commands.LinkCommand;
import io.github.jroy.cowbot.commands.base.CommandFactory;
import io.github.jroy.cowbot.utils.DatabaseFactory;
import io.github.jroy.cowbot.utils.Logger;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.Objects;

public class CowBot extends JavaPlugin implements Listener {

  public static CowBot instance;

  private JDA jda;
  public DatabaseFactory databaseFactory;

  @Override
  public void onEnable() {
    instance = this;
    Logger.log("Loading CowBot...");
    loadConfig();
    getServer().getPluginManager().registerEvents(this, this);

    CommandFactory commandFactory = new CommandFactory("!", ".");
    commandFactory.addCommands(new LinkCommand(this));

    reloadConfig();
    if (getConfig().getString("discord-token") == null || Objects.requireNonNull(getConfig().getString("discord-token")).length() <= 2) {
      Logger.log("Discord token not provided, aborting...");
      Bukkit.getServer().getPluginManager().disablePlugin(this);
    }
    Logger.log("Logging into JDA");
    try {
      jda = new JDABuilder(AccountType.BOT)
          .setToken(getConfig().getString("discord-token"))
          .setStatus(OnlineStatus.DO_NOT_DISTURB)
          .addEventListener(commandFactory.build())
          .build();
    } catch (LoginException e) {
      Logger.log("Could not connect to discord: " + e.getMessage());
    }

    try {
      databaseFactory = new DatabaseFactory(jda);
    } catch (ClassNotFoundException | SQLException e) {
      Logger.log("Unable to connect to the database, aborting...");
      Bukkit.getServer().getPluginManager().disablePlugin(this);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    if (!databaseFactory.isWhitelisted(event.getPlayer().getName())) {
      event.setJoinMessage("");
      event.getPlayer().kickPlayer("You are not whitelisted!\nGive Schlatt Fucking Money\nThen do \"!link " + event.getPlayer().getName() + "\" in the #mc channel on the discord server");
    }
  }

  private void loadConfig() {
    getConfig().addDefault("discord-token", "");
    getConfig().options().copyDefaults(true);
    saveConfig();
  }
}
