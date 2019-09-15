package io.github.jroy.cowbot.commands.proxy;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GlobalSpyCommand extends Command {

  public GlobalSpyCommand() {
    super("gspy", "trevor.admin");
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (!(sender instanceof ProxiedPlayer)) {
      sender.sendMessage(new TextComponent("Player only command!"));
      return;
    }
    ProxiedPlayer player = (ProxiedPlayer) sender;

    if (GlobalMessageCommand.noSpy.contains(player.getName())) {
      GlobalMessageCommand.noSpy.remove(player.getName());
      player.sendMessage(new TextComponent(ChatColor.RED + "Global Social Spy: Disabled!"));
      return;
    }

    GlobalMessageCommand.noSpy.add(player.getName());
    player.sendMessage(new TextComponent(ChatColor.GREEN + "Global Social Spy: Enabled!"));
  }
}
