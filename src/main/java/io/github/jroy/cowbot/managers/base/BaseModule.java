package io.github.jroy.cowbot.managers.base;

public abstract class BaseModule<PluginClass, CommandClass> {

  private final String moduleName;
  protected final PluginClass plugin;

  BaseModule(String moduleName, PluginClass plugin) {
    this.moduleName = moduleName;
    this.plugin = plugin;
  }

  public final void onEnable() {
    long epoch = System.currentTimeMillis();
    log("Initializing...");
    enable();
    addCommands();
    registerSelf();
    log("Enabled in " + (System.currentTimeMillis() - epoch) + " milliseconds.");
  }

  public final void onDisable() {
    disable();
    log("Disabled");
  }

  public void enable() {

  }

  public void disable() {

  }

  public void addCommands() {

  }

  public abstract void addCommand(String name, CommandClass command);

  protected abstract void registerSelf();

  protected void log(String message) {
    System.out.println("[CowBot] " + moduleName + "> " + message);
  }
}
