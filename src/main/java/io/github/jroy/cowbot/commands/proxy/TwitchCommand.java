package io.github.jroy.cowbot.commands.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.managers.proxy.TwitchManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class TwitchCommand extends Command {

  private final ProxiedCow cow;
  private final TwitchManager twitchManager;

  private final HashMap<String, Long> cooldowns = new HashMap<>();

  public TwitchCommand(ProxiedCow cow, TwitchManager twitchManager) {
    super("twitch", null, "discord");
    this.cow = cow;
    this.twitchManager = twitchManager;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (!(sender instanceof ProxiedPlayer)) {
      sender.sendMessage(new TextComponent("This is a player-only command!"));
      return;
    }
    ProxiedPlayer player = (ProxiedPlayer) sender;

    if (!cooldowns.containsKey(player.getName())) {
      cooldowns.put(player.getName(), 0L);
    }

    if (args.length == 0) {
      player.sendMessage(TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "To join the discord, click the following link to authenticate with twitch: " + twitchManager.getOAuthURL()));
      return;
    }

    //Cooldown
    if ((System.currentTimeMillis() - cooldowns.get(player.getName())) < 600000) {
      player.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You can only use this command every 10 minutes!"));
      return;
    }

    cooldowns.put(player.getName(), System.currentTimeMillis());
    ProxyServer.getInstance().getScheduler().runAsync(cow, () -> {
      String auth = args[0];
      try {
        String userId = twitchManager.getUserId(auth);
        String emotes = twitchManager.getUserEmotes(userId, auth);
        if (!emotes.contains("schlattSip")) {
          player.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You are not a twitch subscriber!"));
          return;
        }

        Boolean status = twitchManager.isAuthorized(userId, player.getUniqueId());
        if (status == null) {
          player.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Freeloading!!!"));
          return;
        }

        if (status) {
          twitchManager.addTwitchUser(userId, player.getUniqueId());
        }

        player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Success! Join https://discord.gg/" + twitchManager.createInvite(player.getUniqueId()).getCode()));
      } catch (IOException e) {
        player.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_RED + "Error: " + ChatColor.RED + e.getMessage()));
      } catch (SQLException sqlException) {
        sqlException.printStackTrace();
        player.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_RED + "Error: " + ChatColor.RED + sqlException.getMessage()));
      }
    });
  }
}
