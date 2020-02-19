package io.github.jroy.cowbot.utils;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public final class CartMeta extends FixedMetadataValue {
  public CartMeta(Plugin owningPlugin) {
    super(owningPlugin, null);
  }

  /**
   * Number of ticks to keep the cart slowed after hitting a speed bump.
   * When this has counted down to zero, maximum speed is restored.
   */
  public int slowDownRemainingTicks;

  /**
   * Velocity of the cart in the previous tick. Used to deal with
   * spontaneous velocity reversal, as dicussed in the plugin class doc
   * comment.
   */
  public Vector previousTickVelocity;
}
