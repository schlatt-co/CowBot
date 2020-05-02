package io.github.jroy.cowbot.commands.proxy;

import io.github.jroy.cowbot.managers.proxy.PlayerConnectionManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class LockdownCommand extends Command {

  private final PlayerConnectionManager playerConnectionManager;

  public LockdownCommand(PlayerConnectionManager playerConnectionManager) {
    super("lockdown");
    this.playerConnectionManager = playerConnectionManager;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (sender.hasPermission("cowbot.trevor")) {
      if (args.length == 0) {
        playerConnectionManager.setLockdown(!playerConnectionManager.isLockdown());
        sender.sendMessage(new TextComponent("Lockdown Toggled: " + playerConnectionManager.isLockdown()));
        return;
      }

      if (playerConnectionManager.getLockdownList().contains(args[0])) {
        playerConnectionManager.removeLockdown(args[0]);
        sender.sendMessage(new TextComponent("Removed User from Lockdown Whitelist!"));
      } else {
        playerConnectionManager.addLockdown(args[0]);
        sender.sendMessage(new TextComponent("Added User to Lockdown Whitelist!"));
      }
    }
  }
}
