package io.github.jroy.cowbot.commands;

import io.github.jroy.cowbot.ProxiedCow;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class LockdownCommand extends Command {

  private ProxiedCow proxiedCow;

  public LockdownCommand(ProxiedCow proxiedCow) {
    super("lockdown");
    this.proxiedCow = proxiedCow;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (sender.hasPermission("cowbot.trevor")) {
      if (args.length == 0) {
        proxiedCow.isLockdown = !proxiedCow.isLockdown;
        sender.sendMessage(new TextComponent("Lockdown Toggled: " + proxiedCow.isLockdown));
        return;
      }

      if (proxiedCow.lockdownList.contains(args[0])) {
        proxiedCow.lockdownList.remove(args[0]);
        sender.sendMessage(new TextComponent("Removed User from Lockdown Whitelist!"));
      } else {
        proxiedCow.lockdownList.add(args[0]);
        sender.sendMessage(new TextComponent("Added User to Lockdown Whitelist!"));
      }
    }
  }
}
