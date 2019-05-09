package io.github.jroy.cowbot;

import io.github.jroy.cowbot.commands.CommunismCommand;
import io.github.jroy.cowbot.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CowBot extends JavaPlugin implements Listener {

  private List<Player> sleeping = new ArrayList<>();
  public HashMap<UUID, Boolean> communists = new HashMap<>();

  @Override
  public void onEnable() {
    Logger.log("Loading CowBot...");
    getServer().getPluginManager().registerEvents(this, this);
    getCommand("communism").setExecutor(new CommunismCommand(this));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFirstJoin(PlayerJoinEvent event) {
    if (!event.getPlayer().hasPlayedBefore()) {
      communists.put(event.getPlayer().getUniqueId(), false);
      event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 16, 54, -3));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onLeave(PlayerQuitEvent event) {
    if (communists.containsKey(event.getPlayer().getUniqueId()) && !communists.get(event.getPlayer().getUniqueId())) {
      new File(new File(Bukkit.getServer().getWorld("world").getWorldFolder(), "playerdata"), event.getPlayer().getUniqueId().toString() + ".dat").delete();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event) {
    if (communists.containsKey(event.getPlayer().getUniqueId()) && !communists.get(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage("You must agree to the rules before you can break blocks! Type /communism when you read and agree.");
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (communists.containsKey(event.getPlayer().getUniqueId()) && !communists.get(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage("You must agree to the rules before you can place blocks! Type /communism when you read and agree.");
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSleep(PlayerBedEnterEvent event) {
    if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
      sleeping.add(event.getPlayer());
      if (sleeping.size() >= (event.getPlayer().getWorld().getPlayers().size() / 2)) {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.WHITE + "Advancing to day!");
        //noinspection ConstantConditions
        Bukkit.getServer().getWorld(event.getPlayer().getWorld().getName()).setTime(1000L);
        event.getPlayer().getWorld().setStorm(false);
        event.getPlayer().getWorld().setThundering(false);
        sleeping.clear();
      } else {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.YELLOW + event.getPlayer().getName() + ChatColor.WHITE + " has started sleeping! " + ChatColor.YELLOW + ((event.getPlayer().getWorld().getPlayers().size() / 2) - sleeping.size()) + ChatColor.WHITE + " more player(s) need to sleep in order to advance to day!");
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onUnSleep(PlayerBedLeaveEvent event) {
    sleeping.remove(event.getPlayer());
  }
}
