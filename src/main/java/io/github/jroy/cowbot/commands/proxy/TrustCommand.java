package io.github.jroy.cowbot.commands.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.managers.proxy.DatabaseManager;
import io.github.jroy.cowbot.managers.proxy.PluginMessageManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;

public class TrustCommand extends Command {

  private final ProxiedCow proxiedCow;
  private final DatabaseManager databaseManager;

  public TrustCommand(ProxiedCow proxiedCow, DatabaseManager databaseManager) {
    super("trust");
    this.proxiedCow = proxiedCow;
    this.databaseManager = databaseManager;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (args.length == 0 || !sender.hasPermission("trevor.trust")) {
      sender.sendMessage(new ComponentBuilder(":YEP: NOPE").color(ChatColor.RED).create());
      return;
    }

    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
    if (player == null) {
      sender.sendMessage(new ComponentBuilder("That player is not online!").color(ChatColor.RED).create());
      return;
    }

    boolean isTrusted = databaseManager.isTrusted(player.getUniqueId());
    try {
      if (!isTrusted) {
        databaseManager.addTrusted(player.getUniqueId());
        PluginMessageManager.sendMessage(proxiedCow, "trevor:main", "rauth", player.getName(), "vanilla");
        sender.sendMessage(new ComponentBuilder("Added to trusted!").color(ChatColor.GREEN).create());
        return;
      }
      databaseManager.deleteTrusted(player.getUniqueId());
      PluginMessageManager.sendMessage(proxiedCow, "trevor:main", "notrust", player.getName(), "vanilla");
      sender.sendMessage(new ComponentBuilder("Removed from trusted!").color(ChatColor.GREEN).create());
    } catch (SQLException e) {
      sender.sendMessage(new ComponentBuilder("Error occured while preformaing command, alert josh.").color(ChatColor.RED).create());
      e.printStackTrace();
    }
  }
}
