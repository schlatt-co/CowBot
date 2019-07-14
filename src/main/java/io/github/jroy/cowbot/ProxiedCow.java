package io.github.jroy.cowbot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.commands.discord.BanCommand;
import io.github.jroy.cowbot.commands.discord.EvalCommand;
import io.github.jroy.cowbot.commands.discord.LinkCommand;
import io.github.jroy.cowbot.commands.discord.NameCommand;
import io.github.jroy.cowbot.commands.discord.base.CommandFactory;
import io.github.jroy.cowbot.commands.proxy.LockdownCommand;
import io.github.jroy.cowbot.commands.proxy.StopCommand;
import io.github.jroy.cowbot.commands.proxy.TrevorCommand;
import io.github.jroy.cowbot.utils.ChatEnum;
import io.github.jroy.cowbot.utils.DatabaseFactory;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
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
import java.util.UUID;
import java.util.logging.Level;

@SuppressWarnings("FieldCanBeLocal")
public class ProxiedCow extends Plugin implements Listener {

  public static Configuration configuration;

  private JDA jda;
  private DatabaseFactory databaseFactory;

  private boolean lockdown = false;
  private List<String> lockdownList = new ArrayList<>();

  private String targetVersion = "1.14.3";
  private int targetProtocol = 490;
  private String targetServer = "vanilla";

  public static String serverMotd = "&ajschlatt twitch subscriber server\n&bsubscribe with &5twitch prime&b!";

  @Override
  public void onLoad() {
    log("Hello <3 -Trevor");
  }

  @Override
  public void onEnable() {
    log("Running onEnable flow...");
    if (!loadConfig()) {
      log("The config either couldn't be created or its values were invalid. Proxy is shutting down...");
      getProxy().stop();
      return;
    }

    log("Beginning JDA Login...");
    try {
      jda = new JDABuilder(AccountType.BOT)
          .setToken(configuration.getString("discord-token"))
          .setStatus(OnlineStatus.DO_NOT_DISTURB)
          .addEventListeners(new CommandFactory("!", ".").addCommands(
              new LinkCommand(this),
              new BanCommand(this),
              new NameCommand(this),
              new EvalCommand(this)
          ).build())
          .build().awaitReady();
      log("Logged into JDA!");
    } catch (InterruptedException | LoginException e) {
      getLogger().log(Level.SEVERE, "[Proxy] Error while logging into JDA", e);
      getProxy().stop();
      return;
    }

    log("Beginning Database Connection Initialization...");
    try {
      databaseFactory = new DatabaseFactory(this);
    } catch (SQLException | ClassNotFoundException e) {
      getLogger().log(Level.SEVERE, "[Proxy] Error while initializing database connection", e);
      getProxy().stop();
      return;
    }
    log("Finished Database Connection Initialization!");

    getProxy().getPluginManager().registerListener(this, this);
    getProxy().getPluginManager().registerCommand(this, new TrevorCommand(this));
    getProxy().getPluginManager().registerCommand(this, new LockdownCommand(this));
    getProxy().getPluginManager().registerCommand(this, new StopCommand(this));
    getProxy().registerChannel("trevor:main");
  }

  @EventHandler
  public void onPlayerLogin(LoginEvent event) {
    if (lockdown && !lockdownList.contains(event.getConnection().getName())) {
      event.setCancelReason(new TextComponent("Server is in lockdown white we preform some upgrades :)"));
      event.setCancelled(true);
      return;
    }

    if (!databaseFactory.isWhitelisted(event.getConnection().getName())) {
      event.setCancelReason(new TextComponent(("You are not whitelisted!\nGive Schlatt Fucking Money\nThen do \"!link " + event.getConnection().getName() + "\" in the #mc channel on the discord server")));
      event.setCancelled(true);
      return;
    }

    try {
      databaseFactory.getDiscordIdFromUsername(event.getConnection().getName());
    } catch (SQLException e) {
      //For some reason, trevor is having a problem getting their user id, blame the user
      event.setCancelReason(new TextComponent("Your profile is corrupted!\nPlease re-link your discord account so I can properly segregate you.\n-Trevor"));
      event.setCancelled(true);
      try {
        databaseFactory.deleteUser(event.getConnection().getName());
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }

  @EventHandler
  public void onPlayerConnect(ServerConnectEvent event) {
    if (event.getPlayer().getPendingConnection().getVersion() <= targetProtocol - 1) {
      event.getPlayer().disconnect(new TextComponent("Hey Troglodyte,\nWe updated the server to " + targetVersion + "!\nSo you can't join with whatever shitty version you're on."));
      return;
    }
    event.setTarget(getProxy().getServerInfo(targetServer));
  }

  @SuppressWarnings("UnstableApiUsage")
  @EventHandler
  public void onPluginMessage(PluginMessageEvent event) {
    if (!event.getTag().equalsIgnoreCase("trevor:main")) {
      return;
    }

    ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
    try {
      String subchannel = in.readUTF();
      if (subchannel.equalsIgnoreCase("trevorrequest")) {
        String username = in.readUTF();
        String discordId = databaseFactory.getDiscordIdFromUsername(username);
        @SuppressWarnings("ConstantConditions") Member member = jda.getGuildById("438337215584796692").getMemberById(discordId);
        if (member != null) {
          ChatEnum topRole = null;
          for (Role role : member.getRoles()) {
            switch (role.getId()) {
              case "581340753708449793":
              case "474574079362596864": {
                topRole = checkAndSet(topRole, ChatEnum.UNKNOWN);
                break;
              }
              case "574958723911385098":
              case "556676267357765652": {
                topRole = checkAndSet(topRole, ChatEnum.MOD);
                break;
              }
              case "581910051766272036": {
                topRole = checkAndSet(topRole, ChatEnum.CONTENT);
                break;
              }
              case "509400860132900884":
              case "509400824640700436":
              case "509400795750465596": {
                topRole = checkAndSet(topRole, ChatEnum.PATREON);
                break;
              }
              case "582653648891281409":
              case "525473755712192552":
              case "479782883633135647": {
                topRole = checkAndSet(topRole, ChatEnum.MERCH);
                break;
              }
              case "469920113655808000":
              case "461225008887365632": {
                topRole = checkAndSet(topRole, ChatEnum.TWITCH);
                break;
              }
              default: {
                break;
              }
            }
          }
          if (topRole == null) {
            topRole = ChatEnum.UNKNOWN;
          }
          sendMessage(username + ":" + topRole.name());
        }
      } else if (subchannel.equalsIgnoreCase("target")) {
        targetServer = in.readUTF();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @EventHandler
  public void onServerPing(
      ProxyPingEvent event) {
    ServerPing ping = new ServerPing();
    ping.setDescriptionComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', serverMotd)));
    ping.setVersion(new ServerPing.Protocol(targetVersion, targetProtocol));
    ping.setPlayers(new ServerPing.Players(getProxy().getOnlineCount() + 1, getProxy().getOnlineCount(), new ServerPing.PlayerInfo[]{new ServerPing.PlayerInfo("jschlatt", UUID.fromString("4f16bc54-a296-40ea-bb51-ba6b04ad42c1"))}));
    event.setResponse(ping);
  }

  private ChatEnum checkAndSet(ChatEnum current, ChatEnum change) {
    if (current != null && current.getPower() > change.getPower()) {
      return current;
    }
    return change;
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

  public JDA getJda() {
    return jda;
  }

  public DatabaseFactory getDatabaseFactory() {
    return databaseFactory;
  }

  public void log(String message) {
    getLogger().info("[Proxy] " + message);
  }

  public boolean isLockdown() {
    return lockdown;
  }

  public void setLockdown(boolean lockdown) {
    this.lockdown = lockdown;
  }

  public void addLockdown(String name) {
    lockdownList.add(name);
  }

  public void removeLockdown(String name) {
    lockdownList.remove(name);
  }

  public List<String> getLockdownList() {
    return lockdownList;
  }
}
