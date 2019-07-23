package io.github.jroy.cowbot.commands.spigot;

import io.github.jroy.cowbot.CowBot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ServerCommand implements CommandExecutor {

  private CowBot cowBot;

  public ServerCommand(CowBot cowBot) {
    this.cowBot = cowBot;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!sender.hasPermission("trevor.admin") || !(sender instanceof Player) || args.length == 0) {
      sender.sendMessage("fuck you");
      return true;
    }

    Player target = (Player) sender;
    String dest = args[0];
    if (args.length == 2) {
      Player result = cowBot.getServer().getPlayer(args[0]);
      if (result == null || !result.isOnline()) {
        sender.sendMessage("UNKNOWN");
        return true;
      }
      target = result;
      dest = args[1];
    }

    cowBot.sendToServer(target, dest);
    sender.sendMessage("sent or shit");
    return true;
  }
}
