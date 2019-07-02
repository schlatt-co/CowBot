package io.github.jroy.cowbot.commands.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class LockdownCommand extends Command {

  private ProxiedCow cow;

  public LockdownCommand(ProxiedCow cow) {
    super("lockdown");
    this.cow = cow;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (sender.hasPermission("cowbot.trevor")) {
      if (args.length == 0) {
        cow.setLockdown(!cow.isLockdown());
        sender.sendMessage(new TextComponent("Lockdown Toggled: " + cow.isLockdown()));
        return;
      }

      if (cow.getLockdownList().contains(args[0])) {
        cow.removeLockdown(args[0]);
        sender.sendMessage(new TextComponent("Removed User from Lockdown Whitelist!"));
      } else {
        cow.addLockdown(args[0]);
        sender.sendMessage(new TextComponent("Added User to Lockdown Whitelist!"));
      }
    }
  }
}
