package io.github.jroy.cowbot.managers.spigot;

import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.commands.spigot.CommunismCommand;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CommunismManager extends SpigotModule {

  public final World world = Bukkit.getWorld("world");
  private final Location boxSpawnLocation = new Location(world, 16, 54, -3, -90, 0);
  private final Location boxCornerOne = new Location(world, 6, 66, 7);
  private final Location boxCornerTwo = new Location(world, 26, 51, -13);

  public CommunismManager(CowBot plugin) {
    super("Communism Manager", plugin);
  }

  @Override
  public void addCommands() {
    addCommand("communism", new CommunismCommand(this));
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    if (!event.getPlayer().hasPlayedBefore()) {
      event.getPlayer().teleport(boxSpawnLocation);
    }
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
  public void onCommand(PlayerCommandPreprocessEvent event) {
    if (!event.getMessage().contains("communism") && playerInBox(event.getPlayer())) {
      event.setCancelled(true);
      sendMessage(event.getPlayer());
    }
  }

  private void sendMessage(Player player) {
    player.sendMessage("You must agree to the rules before you can do that! Type /communism when you read and agree to the rules.");
  }

  public boolean playerInBox(Player player){
    if (player.hasPermission("trevor.admin")) {
      return false;
    }

    int x1,x2,y1,y2,z1,z2;
    x1 = boxCornerOne.getX() > boxCornerTwo.getX() ? (int) boxCornerTwo.getX() : (int) boxCornerOne.getX();
    y1 = boxCornerOne.getY() > boxCornerTwo.getY() ? (int) boxCornerTwo.getY() : (int) boxCornerOne.getY();
    z1 = boxCornerOne.getZ() > boxCornerTwo.getZ() ? (int) boxCornerTwo.getZ() : (int) boxCornerOne.getZ();

    x2 = ((int) boxCornerOne.getX()) == x1 ? (int) boxCornerTwo.getX() : (int) boxCornerOne.getX();
    y2 = ((int) boxCornerOne.getY()) == y1 ? (int) boxCornerTwo.getY() : (int) boxCornerOne.getY();
    z2 = ((int) boxCornerOne.getZ()) == z1 ? (int) boxCornerTwo.getZ() : (int) boxCornerOne.getZ();

    for (int x = x1; x <= x2; x++){
      for (int y = y1; y <= y2; y++){
        for (int z = z1; z <= z2; z++){
          if (player.getLocation().getBlock().getLocation().equals(new Location(world, x, y, z))) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
