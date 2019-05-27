package io.github.jroy.cowbot.commands;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.base.CommandBase;
import io.github.jroy.cowbot.commands.base.CommandEvent;

import java.sql.SQLException;

public class BanCommand extends CommandBase {

  private ProxiedCow cowBot;

  public BanCommand(ProxiedCow cowBot) {
    super("ban", "<id> <reason>", "Bans a user from linking their account.", true);
    this.cowBot = cowBot;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()) {
      e.replyError(invalid);
      return;
    }

    if (e.getSplitArgs().length == 1) {
      if (cowBot.databaseFactory.isBanned(e.getSplitArgs()[0])) {
        try {
          cowBot.databaseFactory.pardonUser(e.getSplitArgs()[0]);
          e.reply("Pardoned User!");
        } catch (SQLException ex) {
          e.replyError("Error while pardoning user: " + ex.getMessage());
        }
      } else {
        e.replyError("Please supply a reason to ban the user!");
      }
    } else {
      if (cowBot.databaseFactory.isBanned(e.getSplitArgs()[0])) {
        try {
          cowBot.databaseFactory.pardonUser(e.getSplitArgs()[0]);
          e.reply("Pardoned User!");
        } catch (SQLException ex) {
          e.replyError("Error while pardoning user: " + ex.getMessage());
        }
        return;
      }

      try {
        cowBot.databaseFactory.banUser(e.getSplitArgs()[0], e.getArgs().replace(e.getSplitArgs()[0] + " ", ""));
        e.reply("Banned User!");
      } catch (SQLException ex) {
        e.replyError("Error while banning user: " + ex.getMessage());
      }
    }
  }
}
