package io.github.jroy.cowbot.managers;

import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.ArrayList;
import java.util.List;

public class SleepManager extends SpigotModule {

  private CowBot cowBot;

  private List<Player> sleeping = new ArrayList<>();

  public SleepManager(CowBot cowBot) {
    super("Sleep Manager", cowBot);
    this.cowBot = cowBot;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onSleep(PlayerBedEnterEvent event) {
    if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
      sleeping.add(event.getPlayer());
      event.getPlayer().setStatistic(Statistic.TIME_SINCE_REST, 0);
      Bukkit.broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.YELLOW + event.getPlayer().getName() + ChatColor.WHITE + " has started sleeping! " + ChatColor.YELLOW + ((event.getPlayer().getWorld().getPlayers().size() / 3) - sleeping.size()) + ChatColor.WHITE + " more player(s) need to sleep in order to advance to day!");
      if (sleeping.size() >= (event.getPlayer().getWorld().getPlayers().size() / 3)) {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.WHITE + "Advancing to day in 5 seconds!");
        cowBot.getServer().getScheduler().runTaskLater(cowBot, () -> {
          //noinspection ConstantConditions
          Bukkit.getServer().getWorld(event.getPlayer().getWorld().getName()).setTime(1000L);
          event.getPlayer().getWorld().setStorm(false);
          event.getPlayer().getWorld().setThundering(false);
          sleeping.clear();
        }, 100);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onUnSleep(PlayerBedLeaveEvent event) {
    sleeping.remove(event.getPlayer());
  }
}
