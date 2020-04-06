package io.github.jroy.cowbot.commands.proxy;

import io.github.jroy.cowbot.managers.proxy.PlayerConnectionManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class BungeeWhitelistCommand extends Command {

  private final PlayerConnectionManager playerConnectionManager;

  public BungeeWhitelistCommand(PlayerConnectionManager playerConnectionManager) {
    super("bwhitelist");
    this.playerConnectionManager = playerConnectionManager;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (sender.hasPermission("cowbot.trevor")) {
      playerConnectionManager.setWhitelist(!playerConnectionManager.isWhitelist());
      sender.sendMessage(new TextComponent("Whitelist Toggled: " + playerConnectionManager.isWhitelist()));
    }
  }
}
