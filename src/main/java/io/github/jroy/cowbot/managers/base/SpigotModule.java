package io.github.jroy.cowbot.managers.base;

import io.github.jroy.cowbot.CowBot;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.Objects;

public abstract class SpigotModule extends BaseModule<CowBot, CommandExecutor> implements Listener {

  public SpigotModule(String moduleName, CowBot plugin) {
    super(moduleName, plugin);
  }

  @Override
  public void addCommand(String name, CommandExecutor command) {
    Objects.requireNonNull(plugin.getCommand(name)).setExecutor(command);
  }

  @Override
  protected void registerSelf() {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
}
