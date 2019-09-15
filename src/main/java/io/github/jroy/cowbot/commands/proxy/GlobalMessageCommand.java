package io.github.jroy.cowbot.commands.proxy;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class GlobalMessageCommand extends Command implements TabExecutor {

  static HashSet<String> noSpy = new HashSet<>();

  public GlobalMessageCommand() {
    super("gmsg");
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (!(sender instanceof ProxiedPlayer)) {
      sender.sendMessage(new TextComponent("Player only command!"));
      return;
    }
    ProxiedPlayer player = (ProxiedPlayer) sender;

    if (args.length < 2) {
      sender.sendMessage(new TextComponent(ChatColor.RED + "Correct Usage: /gmsg <player> <message>"));
      return;
    }

    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
    if (target == null) {
      sender.sendMessage(new TextComponent(ChatColor.RED + "User not online!"));
      return;
    }

    StringBuilder msg = new StringBuilder();
    String[] msgParts = args.clone();
    msgParts[0] = "";
    for (String curPart : msgParts) {
      msg.append(curPart).append(" ");
    }

    target.sendMessage(new TextComponent(ChatColor.GREEN + "[" + ChatColor.YELLOW + player.getDisplayName() + ChatColor.GOLD + "->" + ChatColor.YELLOW + "you" + ChatColor.GREEN + "] " + ChatColor.WHITE + msg.toString()));
    player.sendMessage(new TextComponent(ChatColor.GREEN + "[" + ChatColor.YELLOW + "you" + ChatColor.GOLD + "->" + ChatColor.YELLOW + target.getDisplayName() + ChatColor.GREEN + "] " + ChatColor.WHITE + msg.toString()));
    for (ProxiedPlayer curPlayer : ProxyServer.getInstance().getPlayers()) {
      if ((!curPlayer.getName().equals(player.getName()) || !curPlayer.getName().equals(target.getName())) && !noSpy.contains(curPlayer.getName()) && curPlayer.hasPermission("trevor.admin")) {
        curPlayer.sendMessage(new TextComponent(ChatColor.GREEN + "[" + ChatColor.YELLOW + player.getDisplayName() + ChatColor.GOLD + "->" + ChatColor.YELLOW + target.getDisplayName() + ChatColor.GREEN + "] " + ChatColor.WHITE + msg.toString()));
      }
    }
  }

  @Override
  public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
      Set<String> matches = new HashSet<>();
      String search = args[0].toLowerCase(Locale.ROOT);
      for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
        if (player.getName().toLowerCase(Locale.ROOT).startsWith(search)) {
          matches.add(player.getName());
        }
      }
      return matches;
    }
    return ImmutableSet.of();
  }
}
