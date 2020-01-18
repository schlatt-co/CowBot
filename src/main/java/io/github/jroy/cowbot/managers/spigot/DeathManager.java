package io.github.jroy.cowbot.managers.spigot;

import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

public class DeathManager extends SpigotModule {
  private CowBot cowBot;
  public DeathManager(String moduleName, CowBot cowBot) {
    super("Death Manager", cowBot);
    this.cowBot = cowBot;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDeath(PlayerDeathEvent e) {
    Location l = e.getEntity().getLocation();
    System.out.println("Death Info: ");
    System.out.println("World: " + Objects.requireNonNull(l.getWorld()).getName());
    System.out.println("At coords: x=" + l.getBlockX() + ", y=" + l.getBlockY() + ", z=" + l.getBlockZ());
  }
}
