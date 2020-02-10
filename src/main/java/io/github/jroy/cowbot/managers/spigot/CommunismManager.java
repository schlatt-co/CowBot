package io.github.jroy.cowbot.managers.spigot;

import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.commands.spigot.CommunismCommand;
import io.github.jroy.cowbot.commands.spigot.SpawnCommand;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.UUID;

public class CommunismManager extends SpigotModule {

  public final World world = Bukkit.getWorld("world");
  private final Location boxSpawnLocation = new Location(world, 16, 54, -3, -90, 0);
  private final Location boxCornerOne = new Location(world, 6, 66, 7);
  private final Location boxCornerTwo = new Location(world, 26, 51, -13);

  public final HashMap<UUID, Boolean> players = new HashMap<>();

  public CommunismManager(CowBot plugin) {
    super("Communism Manager", plugin);
  }

  @Override
  public void addCommands() {
    addCommand("communism", new CommunismCommand(this));
    addCommand("spawn", new SpawnCommand(this));
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    if (!event.getPlayer().hasPlayedBefore()) {
      event.getPlayer().teleport(boxSpawnLocation);
      players.put(event.getPlayer().getUniqueId(), true);
      return;
    }
    if (playerInBox(event.getPlayer())) {
      players.put(event.getPlayer().getUniqueId(), true);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLeave(PlayerQuitEvent event) {
    players.remove(event.getPlayer().getUniqueId());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event) {
    if (playerInBox(event.getPlayer())) {
      event.setCancelled(true);
      sendMessage(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockPlaceEvent event) {
    if (playerInBox(event.getPlayer())) {
      event.setCancelled(true);
      sendMessage(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent event) {
    if (playerInBox(event.getPlayer())) {
      event.setCancelled(true);
      sendMessage(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFight(EntityDamageEvent event) {
    if (event.getEntityType() == EntityType.PLAYER && playerInBox((Player) event.getEntity())) {
      event.setCancelled(true);
      sendMessage((Player) event.getEntity());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCommand(PlayerCommandPreprocessEvent event) {
    if (!event.getMessage().contains("communism") && playerInBox(event.getPlayer())) {
      event.setCancelled(true);
      sendMessage(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onTeleport(PlayerTeleportEvent event) {
    playerInBox(event.getPlayer(), false);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(PlayerDeathEvent event) {
    playerInBox(event.getEntity(), false);
  }

  private void sendMessage(Player player) {
    player.sendMessage("You must agree to the rules before you can do that! Type /communism when you read and agree to the rules.");
  }

  public boolean playerInBox(Player player) {
    return playerInBox(player, true);
  }

  public boolean playerInBox(Player player, boolean cache) {
    if (player.hasPermission("trevor.admin")) {
      return false;
    }

    if (cache && players.containsKey(player.getUniqueId())) {
      return players.get(player.getUniqueId());
    }

    int x1, x2, y1, y2, z1, z2;
    x1 = boxCornerOne.getX() > boxCornerTwo.getX() ? (int) boxCornerTwo.getX() : (int) boxCornerOne.getX();
    y1 = boxCornerOne.getY() > boxCornerTwo.getY() ? (int) boxCornerTwo.getY() : (int) boxCornerOne.getY();
    z1 = boxCornerOne.getZ() > boxCornerTwo.getZ() ? (int) boxCornerTwo.getZ() : (int) boxCornerOne.getZ();

    x2 = ((int) boxCornerOne.getX()) == x1 ? (int) boxCornerTwo.getX() : (int) boxCornerOne.getX();
    y2 = ((int) boxCornerOne.getY()) == y1 ? (int) boxCornerTwo.getY() : (int) boxCornerOne.getY();
    z2 = ((int) boxCornerOne.getZ()) == z1 ? (int) boxCornerTwo.getZ() : (int) boxCornerOne.getZ();

    for (int x = x1; x <= x2; x++) {
      for (int y = y1; y <= y2; y++) {
        for (int z = z1; z <= z2; z++) {
          if (player.getLocation().getBlock().getLocation().equals(new Location(world, x, y, z))) {
            players.put(player.getUniqueId(), true);
            return true;
          }
        }
      }
    }
    players.put(player.getUniqueId(), false);
    return false;
  }
}
