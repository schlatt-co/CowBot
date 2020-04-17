package io.github.jroy.cowbot.managers.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;

public class NewCuckManager extends SpigotModule {

  private final CowBot cowBot;

  private final HashSet<String> authorizedUsers = new HashSet<>();

  public NewCuckManager(CowBot plugin) {
    super("New Cuck Manager", plugin);
    this.cowBot = plugin;
  }

  public void addAuthorizedUser(String name) {
    authorizedUsers.add(name);
  }

  @SuppressWarnings("UnstableApiUsage")
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Bukkit.getScheduler().runTaskLaterAsynchronously(cowBot, () -> {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("auth|" + event.getPlayer().getName());
      out.writeUTF(event.getPlayer().getName());
      event.getPlayer().sendPluginMessage(cowBot, "trevor:main", out.toByteArray());
    }, 30);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event) {
    authorizedUsers.remove(event.getPlayer().getName());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event) {
    if (isProhibited(event.getPlayer())) {
      event.setCancelled(true);
      sendMessage(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (isProhibited(event.getPlayer())) {
      event.setCancelled(true);
      sendMessage(event.getPlayer());
    }
  }

  private boolean isProhibited(Player player) {
    if (authorizedUsers.contains(player.getName())) {
      return false;
    }
    Location loc= player.getLocation();
    return !(Math.abs(loc.getX()) > 2000) && !(Math.abs(loc.getZ()) > 2000);
  }

  private void sendMessage(Player player) {
    player.sendMessage(ChatColor.RED + "You must be a twitch subscriber to interact with blocks within 2000,2000!");
    player.sendMessage(ChatColor.LIGHT_PURPLE + "You can subscribe at https://twitch.tv/jschlatt");
    player.sendMessage(ChatColor.LIGHT_PURPLE + "Once you subscribe: join the discord (do /discord), read #games-info to join the Minecraft channels, then read #mc-info to link your mc account to twitch!");
    player.sendMessage(ChatColor.RED + "To randomly teleport past 2000, 2000: do /rtp");
  }
}
