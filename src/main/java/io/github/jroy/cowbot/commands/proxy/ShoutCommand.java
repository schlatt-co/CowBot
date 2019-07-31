package io.github.jroy.cowbot.commands.proxy;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ShoutCommand extends Command {

  public ShoutCommand() {
    super("shout");
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (!(sender instanceof ProxiedPlayer)) {
      return;
    }
    if (args.length == 0) {
      sender.sendMessage(new TextComponent("Command Usage: /shout <message>"));
      return;
    }

    ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.RED + "[SHOUT] " + ChatColor.GRAY + "<" + ChatColor.AQUA  + ((ProxiedPlayer) sender).getDisplayName() + ChatColor.GRAY + "> " + ChatColor.WHITE + String.join(" ", args)));
  }
}
