package io.github.jroy.cowbot.commands.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.managers.proxy.PluginMessageManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class RestartCommand extends Command {

  private final ProxiedCow proxiedCow;

  public RestartCommand(ProxiedCow proxiedCow) {
    super("grestart");
    this.proxiedCow = proxiedCow;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (sender.hasPermission("cowbot.trevor")) {
      if (args.length == 0) {
        sender.sendMessage(new TextComponent("Correct Usage: /restart <server>"));
        return;
      }

      if (!ProxyServer.getInstance().getServers().containsKey(args[0])) {
        sender.sendMessage(new TextComponent("Invalid Server!"));
        return;
      }
      PluginMessageManager.sendMessage(proxiedCow, "trevor:discord", "cmd", "restart", args[0]);
      sender.sendMessage(new TextComponent("Requested a restart on that server"));
    }
  }
}
