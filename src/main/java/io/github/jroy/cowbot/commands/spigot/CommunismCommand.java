package io.github.jroy.cowbot.commands.spigot;

import io.github.jroy.cowbot.CowBot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommunismCommand implements CommandExecutor {

  private CowBot cow;

  public CommunismCommand(CowBot cow) {
    this.cow = cow;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("This command may not be run inside the console!");
      return true;
    }
    Player p = (Player) sender;

    if (cow.communists.containsKey(p.getUniqueId()) && !cow.communists.get(p.getUniqueId())) {
      cow.communists.put(p.getUniqueId(), true);
      //noinspection ConstantConditions
      p.teleport(Bukkit.getWorld("world").getSpawnLocation());
    } else {
      p.sendMessage("You're already a communist!");
    }
    return true;
  }
}
