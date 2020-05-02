package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import io.github.jroy.cowbot.managers.proxy.DiscordManager;
import io.github.jroy.cowbot.managers.proxy.discord.Roles;

public class NoEventsCommand extends CommandBase {

  private final DiscordManager discordManager;

  public NoEventsCommand(DiscordManager discordManager) {
    super("noevents", "", "Removes you from event ping role.");
    this.discordManager = discordManager;
  }

  @Override
  protected void executeCommand(CommandEvent event) {
    discordManager.toggleRole(event.getMember(), Roles.MC_EVENTS, false);
    event.getMessage().delete().queue();
  }
}
