package io.github.jroy.cowbot.commands.spigot;

import io.github.jroy.cowbot.managers.spigot.CommunismManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommunismCommand implements CommandExecutor {

  private CommunismManager communismManager;

  public CommunismCommand(CommunismManager communismManager) {
    this.communismManager = communismManager;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("This command may not be run inside the console!");
      return true;
    }
    Player p = (Player) sender;

    if (communismManager.playerInBox(p)) {
      assert communismManager.world != null;
      p.teleport(communismManager.world.getSpawnLocation());
    } else {
      p.sendMessage("You're already a communist!");
    }
    return true;
  }
}
