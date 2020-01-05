package io.github.jroy.cowbot.commands.spigot;

import io.github.jroy.cowbot.managers.spigot.CommunismManager;
import io.papermc.lib.PaperLib;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

  private final CommunismManager communismManager;

  public SpawnCommand(CommunismManager communismManager) {
    this.communismManager = communismManager;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player) || !sender.hasPermission("trevor.spawn")) {
      return false;
    }
    Player player = (Player) sender;

    assert communismManager.world != null;
    PaperLib.teleportAsync(player, communismManager.world.getSpawnLocation());
    return true;
  }
}
