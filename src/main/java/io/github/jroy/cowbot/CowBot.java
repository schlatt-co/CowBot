package io.github.jroy.cowbot;

import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.managers.spigot.*;
import io.github.jroy.cowbot.utils.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Bat;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Squid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class CowBot extends JavaPlugin implements Listener {

  private List<SpigotModule> loadedModules = new ArrayList<>();

  private WebhookManager webhookManager;
  private ChatManager chatManager;

  private ServerType currentServer = ServerType.UNKNOWN;

  @Override
  public void onEnable() {
    log("Loading CowBot...");
    for (ServerType type : ServerType.values()) {
      if (type.equals(ServerType.UNKNOWN)) {
        continue;
      }
      if (Bukkit.getWorld(type.getWorldName()) != null) {
        currentServer = type;
      }
    }
    log("Detected Server Type: " + currentServer.name());
    getServer().getPluginManager().registerEvents(this, this);
    if (currentServer.equals(ServerType.VANILLA)) {
      loadedModules.add(new CommunismManager(this));
      loadedModules.add(new SleepManager(this));
      loadedModules.add(new CartManager(this));
    }
    loadedModules.add(webhookManager = new WebhookManager(this));
    loadedModules.add(chatManager = new ChatManager(this));
    loadedModules.add(new PluginMessageManager(this, webhookManager, chatManager));
    if (getServer().getPluginManager().getPlugin("Stonks") != null) {
      log("Loading Stonks Integration...");
      loadedModules.add(new HomeManager(this));
    }

    for (SpigotModule module : loadedModules) {
      module.onEnable();
    }

    getServer().addRecipe(new ShapelessRecipe(new NamespacedKey(this, "honeycomb"),
        new ItemStack(Material.HONEYCOMB, 4))
        .addIngredient(1, Material.HONEYCOMB_BLOCK));
  }

  public ServerType getCurrentServer() {
    return currentServer;
  }

  @Override
  public void onDisable() {
    for (SpigotModule module : loadedModules) {
      module.onDisable();
    }
  }

  /**
   * This helps with a bug in minecraft which causes bats and fish not counting towards to mob cap.
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (event.getEntity() instanceof Bat || event.getEntity() instanceof FishHook || event.getEntity() instanceof Squid || event.getEntity() instanceof Pillager) {
      event.setCancelled(true);
    }
  }

  private void log(String message) {
    getLogger().info("[SPIGOT] " + message);
  }
}
