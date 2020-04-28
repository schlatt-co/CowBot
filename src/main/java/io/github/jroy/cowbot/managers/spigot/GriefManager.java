package io.github.jroy.cowbot.managers.spigot;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import org.bukkit.event.EventHandler;

public class GriefManager extends SpigotModule {

  public GriefManager(CowBot plugin) {
    super("Grief Manager", plugin);
  }

  @EventHandler
  public void onTntPrime(TNTPrimeEvent event) {
    event.setCancelled(true);
  }
}
