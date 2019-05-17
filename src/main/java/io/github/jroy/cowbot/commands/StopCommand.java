package io.github.jroy.cowbot.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class StopCommand extends Command {

  public StopCommand() {
    super("quit");
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (!sender.hasPermission("cowbot.trevor")) {
      return;
    }
    ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> proxiedPlayer.disconnect(new TextComponent("Proxy shutting down, we'll be back!")));
    ProxyServer.getInstance().stop();
  }
}
