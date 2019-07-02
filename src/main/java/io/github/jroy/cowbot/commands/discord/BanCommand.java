package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;

import java.sql.SQLException;

public class BanCommand extends CommandBase {

  private ProxiedCow cow;

  public BanCommand(ProxiedCow cow) {
    super("ban", "<id> <reason>", "Bans a user from linking their account.", true);
    this.cow = cow;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()) {
      e.replyError(invalid);
      return;
    }

    if (e.getSplitArgs().length == 1) {
      if (cow.getDatabaseFactory().isBanned(e.getSplitArgs()[0])) {
        try {
          cow.getDatabaseFactory().pardonUser(e.getSplitArgs()[0]);
          e.reply("Pardoned User!");
        } catch (SQLException ex) {
          e.replyError("Error while pardoning user: " + ex.getMessage());
        }
      } else {
        e.replyError("Please supply a reason to ban the user!");
      }
    } else {
      if (cow.getDatabaseFactory().isBanned(e.getSplitArgs()[0])) {
        try {
          cow.getDatabaseFactory().pardonUser(e.getSplitArgs()[0]);
          e.reply("Pardoned User!");
        } catch (SQLException ex) {
          e.replyError("Error while pardoning user: " + ex.getMessage());
        }
        return;
      }

      try {
        cow.getDatabaseFactory().banUser(e.getSplitArgs()[0], e.getArgs().replace(e.getSplitArgs()[0] + " ", ""));
        e.reply("Banned User!");
      } catch (SQLException ex) {
        e.replyError("Error while banning user: " + ex.getMessage());
      }
    }
  }
}
