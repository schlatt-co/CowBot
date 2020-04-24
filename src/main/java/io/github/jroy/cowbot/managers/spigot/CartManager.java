package io.github.jroy.cowbot.managers.spigot;

import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.utils.CartMeta;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class CartManager extends SpigotModule {

  private final CowBot plugin;

  public CartManager(CowBot plugin) {
    super("Cart Manager", plugin);
    this.plugin = plugin;
  }

  @EventHandler(ignoreCancelled = true)
  public void onVehicleCreate(VehicleCreateEvent event) {
    if (event.getVehicle() instanceof Minecart) {
      ((Minecart) event.getVehicle()).setMaxSpeed(plugin.getConfig().getDouble("cart"));
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
            cart.setMaxSpeed(plugin.getConfig().getDouble("cart"));
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
      meta = new CartMeta(plugin);
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
        (b.getType() == Material.POWERED_RAIL && b.isBlockPowered())) {
      return shape == Rail.Shape.ASCENDING_NORTH ||
          shape == Rail.Shape.ASCENDING_SOUTH ||
          shape == Rail.Shape.ASCENDING_EAST ||
          shape == Rail.Shape.ASCENDING_WEST;
    }
    return false;
  }
}
