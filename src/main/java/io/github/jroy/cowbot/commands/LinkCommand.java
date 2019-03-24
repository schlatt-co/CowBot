package io.github.jroy.cowbot.commands;

import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.commands.base.CommandBase;
import io.github.jroy.cowbot.commands.base.CommandEvent;

import java.sql.SQLException;

public class LinkCommand extends CommandBase {

  private CowBot cowBot;

  public LinkCommand(CowBot cowBot) {
    super("link", "<minecraft username>", "Links your minecraft username with your discord account.");
    this.cowBot = cowBot;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.getTextChannel().getId().equalsIgnoreCase("559192147866419211")) {
      return;
    }

    if (e.getArgs().isEmpty()) {
      e.reply(invalid);
      return;
    }

    if (cowBot.databaseFactory.isLinked(e.getMember().getUser().getId())) {
      try {
        cowBot.databaseFactory.updateUser(e.getMember().getUser().getId(), e.getSplitArgs()[0]);
        e.reply("Updated your current Minecraft name!");
      } catch (SQLException e1) {
        e.reply("Error while updating your Minecraft name...");
      }
    } else {
      try {
        cowBot.databaseFactory.linkUser(e.getMember().getUser().getId(), e.getSplitArgs()[0]);
        e.reply("Added you to the whitelist!");
      } catch (SQLException e1) {
        e.reply("Error while adding you to the whitelist...");
      }
    }
  }
}
