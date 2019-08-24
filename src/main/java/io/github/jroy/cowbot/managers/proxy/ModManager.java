package io.github.jroy.cowbot.managers.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.managers.base.ProxyModule;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.packet.PluginMessage;

import java.util.concurrent.TimeUnit;

public class ModManager extends ProxyModule {

  private final ProxiedCow plugin;

  public ModManager(ProxiedCow plugin) {
    super("Mod Manager", plugin);
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPostLogin(PostLoginEvent event) {
    ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
      //Badlion Client Configs
      event.getPlayer().unsafe().sendPacket(new PluginMessage("badlion:mods", "{\"MiniMap\":{\"disabled\":true},\"ToggleSneak\":{\"disabled\":false,\"extra_data\":{\"inventorySneak\":{\"disabled\":true},\"flySpeed\":{\"disabled\":true}}}}".getBytes(), false));
      //VoxelMap Kill Code
      event.getPlayer().sendMessage(new TextComponent("§3 §6 §3 §6 §3 §6 §e §3 §6 §3 §6 §3 §6 §d"));
    }, 2, TimeUnit.SECONDS);
  }
}
