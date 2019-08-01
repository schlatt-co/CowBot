package io.github.jroy.cowbot.commands.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.managers.proxy.PluginMessageManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class DispatchCommand extends Command {

  private ProxiedCow proxiedCow;

  public DispatchCommand(ProxiedCow proxiedCow) {
    super("dispatch");
    this.proxiedCow = proxiedCow;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (sender.hasPermission("cowbot.trevor")) {
      if (args.length < 2) {
        sender.sendMessage(new TextComponent("Correct Usage: /dispatch <server> <command>"));
        return;
      }

      if (!ProxyServer.getInstance().getServers().containsKey(args[0])) {
        sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid Server!"));
        return;
      }
      String command = String.join(" ", args).replaceFirst(args[0] + " ", "");

      PluginMessageManager.sendMessage(proxiedCow, "trevor:discord", "dispatch", sender.getName() + ":" + command, args[0]);
    }
  }
}
