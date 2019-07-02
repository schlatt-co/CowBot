package io.github.jroy.cowbot.commands.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class StopCommand extends Command {

  private ProxiedCow cow;

  public StopCommand(ProxiedCow cow) {
    super("quit");
    this.cow = cow;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (!sender.hasPermission("cowbot.trevor")) {
      return;
    }
    cow.getProxy().getPlayers().forEach(proxiedPlayer -> proxiedPlayer.disconnect(new TextComponent("Proxy shutting down, we'll be back!")));
    cow.getProxy().stop();
  }
}
