package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import io.github.jroy.cowbot.managers.proxy.DiscordManager;
import io.github.jroy.cowbot.managers.proxy.PluginMessageManager;

public class ClearCacheCommand extends CommandBase {

  private final DiscordManager discordManager;

  public ClearCacheCommand(DiscordManager discordManager) {
    super("clearcache", "<username>", "Clears a user from the chatenum cache");
    this.discordManager = discordManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.hasRole("583031536639541248") || !e.hasRole("594362773266366475")) {
      e.replyError("No permission!");
      return;
    }
    if (e.getArgs().isEmpty()) {
      e.replyError(invalid);
      return;
    }
    PluginMessageManager.sendMessage(discordManager.getProxiedCow(), "trevor:main", "clear", e.getSplitArgs()[0], "vanilla");
    e.reply("Sent cache clear packet!");
  }
}
