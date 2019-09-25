package io.github.jroy.cowbot.commands.spigot;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DirtCommand implements CommandExecutor {

  private final ItemStack dirt;

  public DirtCommand() {
    this.dirt = new ItemStack(Material.DIRT);
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player) || !sender.hasPermission("trevor.dirt")) {
      return false;
    }
    Player player = (Player) sender;

    newDirt();
    player.getInventory().addItem(dirt);
    player.sendMessage("dirt");
    return true;
  }

  private void newDirt() {
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.RED + "Dirt ID; " + UUID.randomUUID().toString());
    dirt.setLore(lore);
  }
}
