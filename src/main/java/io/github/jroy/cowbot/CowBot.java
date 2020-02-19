package io.github.jroy.cowbot;

import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.managers.spigot.*;
import io.github.jroy.cowbot.utils.CartMeta;
import io.github.jroy.cowbot.utils.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.metadata.MetadataValue;
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

  @EventHandler(ignoreCancelled = true)
  public void onVehicleCreate(VehicleCreateEvent event) {
    if (event.getVehicle() instanceof Minecart) {
      ((Minecart) event.getVehicle()).setMaxSpeed(getConfig().getDouble("cart"));
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onVehicleMove(VehicleMoveEvent event) {
    if (event.getVehicle() instanceof Minecart) {
      Minecart cart = (Minecart) event.getVehicle();
      CartMeta meta = getCartMeta(cart);
      Block toBlock = event.getTo().getBlock();

      if (isRail(toBlock)) {
        if (shouldTakeSlow(toBlock)) {
          if (cart.getMaxSpeed() > 0.4D) {
            if (meta.previousTickVelocity != null) {
              cart.setVelocity(meta.previousTickVelocity);
            }
            cart.setMaxSpeed(0.4D);
            // Reset the count down to full speed.
            meta.slowDownRemainingTicks = 40;

          }
        } else {
          // Count down the ticks before setting full speed.
          if (--meta.slowDownRemainingTicks <= 0) {
            // Set max speed EVERY tick to track the /cart-speed.
            cart.setMaxSpeed(getConfig().getDouble("cart"));
          }
        }
      }
      meta.previousTickVelocity = cart.getVelocity();
    }
  }

  private CartMeta getCartMeta(Minecart cart) {
    List<MetadataValue> metaList = cart.getMetadata("TrevorCart");
    CartMeta meta = (metaList.size() == 1) ? (CartMeta) metaList.get(0) : null;
    if (meta == null) {
      meta = new CartMeta(this);
      cart.setMetadata("TrevorCart", meta);
    }
    return meta;
  }

  private boolean isRail(Block b) {
    return b.getType() == Material.RAIL ||
        b.getType() == Material.POWERED_RAIL ||
        b.getType() == Material.DETECTOR_RAIL ||
        b.getType() == Material.ACTIVATOR_RAIL;
  }

  private boolean shouldTakeSlow(Block b) {
    Rail rail = (Rail) b.getBlockData();
    Rail.Shape shape = rail.getShape();
    if (b.getType() == Material.RAIL) {
      // Ramps and curves of regular rail.
      return (shape != Rail.Shape.NORTH_SOUTH && shape != Rail.Shape.EAST_WEST);
    } else if (b.getType() == Material.DETECTOR_RAIL ||
        b.getType() == Material.ACTIVATOR_RAIL ||
        (b.getType() == Material.POWERED_RAIL && b.getBlockPower() != 0)) {
      return shape == Rail.Shape.ASCENDING_NORTH ||
          shape == Rail.Shape.ASCENDING_SOUTH ||
          shape == Rail.Shape.ASCENDING_EAST ||
          shape == Rail.Shape.ASCENDING_WEST;
    }
    return false;
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
