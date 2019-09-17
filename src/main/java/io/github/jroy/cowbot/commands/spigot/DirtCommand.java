package io.github.jroy.cowbot.commands.spigot;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DirtCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player) || !sender.hasPermission("trevor.dirt")) {
      return false;
    }
    Player player = (Player) sender;

    player.getInventory().addItem(new ItemStack(Material.DIRT));
    player.sendMessage("dirt");
    return true;
  }
}
