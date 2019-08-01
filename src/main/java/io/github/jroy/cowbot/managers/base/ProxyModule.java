package io.github.jroy.cowbot.managers.base;

import io.github.jroy.cowbot.ProxiedCow;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

public abstract class ProxyModule extends BaseModule<ProxiedCow, Command> implements Listener {

  ProxyModule(String moduleName, ProxiedCow plugin) {
    super(moduleName, plugin);
  }

  @Override
  public void addCommand(String name, Command command) {
    plugin.getProxy().getPluginManager().registerCommand(plugin, command);
  }

  public void addCommand(Command command) {
    addCommand("", command);
  }

  @Override
  protected void registerSelf() {
    plugin.getProxy().getPluginManager().registerListener(plugin, this);
  }
}
