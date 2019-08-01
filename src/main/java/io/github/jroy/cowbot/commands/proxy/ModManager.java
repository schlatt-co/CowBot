package io.github.jroy.cowbot.commands.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.managers.base.ProxyModule;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ModManager extends ProxyModule {

  public ModManager(ProxiedCow plugin) {
    super("Mod Manager", plugin);
  }

  @EventHandler
  public void onPostLogin(PostLoginEvent event) {
    //Badlion Client Configs
    event.getPlayer().unsafe().sendPacket(new PluginMessage("badlion:mods", "{\"MiniMap\":{\"disabled\":true},\"ToggleSneak\":{\"disabled\":false,\"extra_data\":{\"inventorySneak\":{\"disabled\":true},\"flySpeed\":{\"disabled\":true}}}}".getBytes(), false));
    //VoxelMap Kill Code
    event.getPlayer().sendMessage(new TextComponent("§3 §6 §3 §6 §3 §6 §e §3 §6 §3 §6 §3 §6 §d"));
  }
}
