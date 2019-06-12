package io.github.jroy.cowbot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.commands.*;
import io.github.jroy.cowbot.commands.base.CommandFactory;
import io.github.jroy.cowbot.utils.ChatEnum;
import io.github.jroy.cowbot.utils.DatabaseFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
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
  private JDA jda;
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
      getProxy().registerChannel("trevor:main");
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
  public void onPluginMessage(PluginMessageEvent event) {
    if (!event.getTag().equalsIgnoreCase("trevor:main")) {
      return;
    }

    ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
    try {
      if (in.readUTF().equalsIgnoreCase("trevorrequest")) {
        String username = in.readUTF();
        String discordId = databaseFactory.getDiscordIdFromUsername(username);
        Member member = jda.getGuildById("438337215584796692").getMemberById(discordId);
        if (member != null) {
          ChatEnum topRole = ChatEnum.UNKNOWN;
          // R:#Content(581910051766272036),
          // R:The Only Montana Men(556676267357765652),
          // R:Old Father(509400860132900884),
          // R:Merch Gang III(582653648891281409),
          // R:Twitch Mod(574958723911385098),
          // R:First Patron(458726727149944852),
          // R:Lucid(560179472339435576),
          // R:Butler(466345657607782411),
          // R:#statenislandgang(499270025379840001),
          // R:Sysadmin(474574079362596864),
          // R:Risen Gamer(509400824640700436),
          // R:Merch Gang I(479782883633135647),
          // R:Merch Gang II(525473755712192552),
          // R:Gamer(509400795750465596),
          // R:YouTube Sponsors(469920113655808000),
          // R:Twitch Subscribers(461225008887365632),
          // R:Skin Daddy(548901447333314560),
          // R:OG(581340753708449793),
          // R:Has Swag(509390992655253514),
          // R:Schlatt&Coin(546207945814179840),
          // R:Trevor from Cowchop(559190002660278283),
          // R:Nitro Booster(585535513230835742),
          // R:@everyone(438337215584796692)]
          for (Role role : member.getRoles()) {
            switch (role.getId()) {
              case "581340753708449793":
              case "474574079362596864": {
                topRole = ChatEnum.UNKNOWN;
              }
              case "574958723911385098":
              case "556676267357765652": {
                topRole = ChatEnum.MOD;
                break;
              }
              case "581910051766272036": {
                topRole = ChatEnum.CONTENT;
                break;
              }
              case "509400860132900884":
              case "509400824640700436":
              case "509400795750465596": {
                topRole = ChatEnum.PATREON;
                break;
              }
              case "469920113655808000":
              case "461225008887365632": {
                topRole = ChatEnum.TWITCH;
                break;
              }
              default: {
                break;
              }
            }
          }
          sendMessage(username + ":" + topRole.name());
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void sendMessage(String message) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(stream);
    try {
      out.writeUTF("trevorreturn");
      out.writeUTF(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
    getProxy().getServerInfo("vanilla").sendData("trevor:main", stream.toByteArray());
  }

  @EventHandler
  public void onServerConnectEvent(ServerConnectEvent event) {
    if (event.getPlayer().getPendingConnection().getVersion() <= 484) {
      if (databaseFactory.isVive(event.getPlayer().getName())) {
        event.setTarget(getProxy().getServerInfo("vivecraft"));
      } else {
        event.getPlayer().disconnect(new TextComponent("Hey Troglodyte,\nWe updated the server to 1.14.2!\nSo you can't join with whatever shitty version you're on."));
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
