package io.github.jroy.cowbot.managers.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.proxy.BungeeWhitelistCommand;
import io.github.jroy.cowbot.commands.proxy.LockdownCommand;
import io.github.jroy.cowbot.commands.proxy.StopCommand;
import io.github.jroy.cowbot.managers.base.ProxyModule;
import io.github.jroy.cowbot.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.event.EventHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class PlayerConnectionManager extends ProxyModule {

  private final ProxiedCow proxiedCow;
  private final DatabaseManager databaseManager;
  private final List<String> lockdownList = new ArrayList<>();
  private final HashSet<String> whitelistList = new HashSet<>();
  private final String targetVersion = "1.16.1";
  private final int targetProtocol = 736;
  private boolean whitelist = false;
  private boolean lockdown = false;
  private String targetServer = "vanilla";
  private String serverMotd = "&ajschlatt twitch subscriber server\n&5thinkin' about nether";

  {
    lockdownList.add("WheezyGold7931");
    lockdownList.add("iliketanks1998");
    lockdownList.add("Tsarcasm");
  }

  public PlayerConnectionManager(ProxiedCow proxiedCow, DatabaseManager databaseManager) {
    super("Player Connection Manager", proxiedCow);
    this.proxiedCow = proxiedCow;
    this.databaseManager = databaseManager;
  }

  @Override
  public void addCommands() {
    addCommand(new LockdownCommand(this));
    addCommand(new StopCommand(proxiedCow));
    addCommand(new BungeeWhitelistCommand(this));
  }

  @EventHandler
  public void onPlayerLogin(LoginEvent event) {
    if (lockdown && !lockdownList.contains(event.getConnection().getName())) {
      event.setCancelReason(new TextComponent("Server is in lockdown white we preform some upgrades :)"));
      event.setCancelled(true);
      return;
    }

    if (!databaseManager.isWhitelisted(event.getConnection().getName())) {
      if (whitelist) {
        event.setCancelReason(new TextComponent(("You are not whitelisted!\nGive Schlatt Fucking Money\nThen do \"!link " + event.getConnection().getName() + "\" in the #mc channel on the discord server")));
        event.setCancelled(true);
        return;
      }
      if (databaseManager.isTrusted(event.getConnection().getUniqueId())) {
        PluginMessageManager.sendMessage(proxiedCow, "trevor:main", "rauth", event.getConnection().getName(), targetServer);
      }
      return;
    }

    whitelistList.add(event.getConnection().getName());
    PluginMessageManager.sendMessage(proxiedCow, "trevor:main", "rauth", event.getConnection().getName(), targetServer);

    try {
      databaseManager.getDiscordIdFromUsername(event.getConnection().getName());
    } catch (SQLException e) {
      //For some reason, trevor is having a problem getting their user id, blame the user
      event.setCancelReason(new TextComponent("Your profile is corrupted!\nPlease re-link your discord account so I can properly segregate you.\n-Trevor"));
      event.setCancelled(true);
      try {
        databaseManager.deleteUser(event.getConnection().getName());
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }

  @EventHandler()
  public void onPlayerConnect(ServerConnectEvent event) {
    if (event.getPlayer().getPendingConnection().getVersion() <= targetProtocol - 1) {
      event.getPlayer().disconnect(new TextComponent("Hey Troglodyte,\nWe updated the server to " + targetVersion + "!\nSo you can't join with whatever shitty version you're on."));
      return;
    }
    if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
      event.setTarget(proxiedCow.getProxy().getServerInfo(targetServer));
    }
  }

  @EventHandler
  public void onPlayerLeave(PlayerDisconnectEvent event) {
    whitelistList.remove(event.getPlayer().getName());
  }

  @EventHandler
  public void onServerPing(ProxyPingEvent event) {
    ServerPing ping = new ServerPing();
    ping.setDescriptionComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', serverMotd)));
    ping.setVersion(new ServerPing.Protocol(targetVersion, targetProtocol));
    ping.setPlayers(new ServerPing.Players(proxiedCow.getProxy().getOnlineCount() + 1, proxiedCow.getProxy().getOnlineCount(), new ServerPing.PlayerInfo[]{new ServerPing.PlayerInfo("jschlatt", UUID.fromString(Constants.JSCHALTT_UUID))}));
    ping.setFavicon(event.getResponse().getFaviconObject());
    event.setResponse(ping);
  }

  public boolean isAuthed(String name) {
    return whitelistList.contains(name);
  }

  public boolean isWhitelist() {
    return whitelist;
  }

  public void setWhitelist(boolean whitelist) {
    this.whitelist = whitelist;
  }

  public boolean isLockdown() {
    return lockdown;
  }

  public void setLockdown(boolean lockdown) {
    this.lockdown = lockdown;
  }

  public List<String> getLockdownList() {
    return lockdownList;
  }

  public void addLockdown(String player) {
    lockdownList.add(player);
  }

  public void removeLockdown(String player) {
    lockdownList.remove(player);
  }

  void setTargetServer(String targetServer) {
    this.targetServer = targetServer;
  }

  public void setServerMotd(String serverMotd) {
    this.serverMotd = serverMotd;
  }
}
