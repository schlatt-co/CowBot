package io.github.jroy.cowbot.commands.spigot;

import com.earth2me.essentials.Essentials;
import io.github.jroy.cowbot.managers.spigot.CommunismManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

  private final CommunismManager communismManager;
  private final Essentials essentials;

  public SpawnCommand(CommunismManager communismManager) {
    this.communismManager = communismManager;
    this.essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player) || !sender.hasPermission("trevor.spawn")) {
      return false;
    }
    Player player = (Player) sender;

    assert communismManager.world != null;
    try {
      essentials.getUser(player).getTeleport().now(communismManager.world.getSpawnLocation(), false, PlayerTeleportEvent.TeleportCause.COMMAND);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }
}
