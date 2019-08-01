package io.github.jroy.cowbot.utils;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncFinishedChatEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  private final String prefix;
  private final String displayName;
  private final String message;

  public AsyncFinishedChatEvent(String prefix, String displayName, String message) {
    super(true);
    this.prefix = prefix;
    this.displayName = displayName;
    this.message = message;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }
}
