package io.github.jroy.cowbot.commands;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.base.CommandBase;
import io.github.jroy.cowbot.commands.base.CommandEvent;

import java.sql.SQLException;

public class ViveCommand extends CommandBase {

  private ProxiedCow cowBot;

  public ViveCommand(ProxiedCow cowBot) {
    super("vive", "<mc name>", "Adds a user to the vivecraft server.", true);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()) {
      e.replyError(invalid);
      return;
    }

    if (cowBot.databaseFactory.isVive(e.getSplitArgs()[0])) {
      try {
        cowBot.databaseFactory.deleteVive(e.getSplitArgs()[0]);
        e.reply("Deleted user from the vivecraft whitelist!");
      } catch (SQLException ex) {
        e.replyError("I fucking hate the vive and refuse to delete this user!");
      }
      return;
    }
    try {
      cowBot.databaseFactory.linkVive(e.getSplitArgs()[0]);
      e.reply("Added user to the vivecraft whitelist!");
    } catch (SQLException ex) {
      e.replyError("I fucking hate the vive and refuse to add this user!");
    }
  }
}
