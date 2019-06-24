package io.github.jroy.cowbot.commands;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.base.CommandBase;
import io.github.jroy.cowbot.commands.base.CommandEvent;
import io.github.jroy.cowbot.utils.ATLauncherUtils;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.SQLException;

public class LinkCommand extends CommandBase {

  private ProxiedCow cowBot;

  public LinkCommand(ProxiedCow cowBot) {
    super("link", "<minecraft username>", "Links your minecraft username with your discord account.");
    this.cowBot = cowBot;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.getTextChannel().getId().equalsIgnoreCase("559192147866419211")) {
      return;
    }

    if (e.getMember().getRoles().isEmpty()) {
      e.replyError("Hey loser, give Schlatt money and MAYBE I'll consider letting you in the server you fucking free-loader commie.");
      return;
    }

    if (cowBot.databaseFactory.isBanned(e.getMember().getUser().getId())) {
      try {
        e.replyError("You are banned from the server for the following reason: " + cowBot.databaseFactory.getBanReason(e.getMember().getUser().getId()));
      } catch (SQLException ex) {
        e.replyError("You're banned but I don't care enough to fetch the reason.");
      }
      return;
    }

    if (e.getArgs().isEmpty()) {
      e.reply(invalid);
      return;
    }

    if (cowBot.databaseFactory.isLinked(e.getMember().getUser().getId())) {
      try {
        String pastName = cowBot.databaseFactory.getUsernameFromDiscordId(e.getMember().getUser().getId());
        cowBot.databaseFactory.updateUser(e.getMember().getUser().getId(), e.getSplitArgs()[0]);
        e.reply("Updated " + e.getMember().getAsMention() + "'s current Minecraft name to " + e.getSplitArgs()[0]);
        if (cowBot.getProxy().getPlayer(pastName) != null) {
          cowBot.getProxy().getPlayer(pastName).disconnect(new TextComponent("Updated your username!"));
        }
        ATLauncherUtils.removePlayer(pastName);
        new Thread(() -> ATLauncherUtils.addPlayer(e.getSplitArgs()[0])).start();
      } catch (SQLException e1) {
        e.reply("Error while updating your Minecraft name...");
      }
    } else {
      try {
        cowBot.databaseFactory.linkUser(e.getMember().getUser().getId(), e.getSplitArgs()[0]);
        e.reply("Added " + e.getMember().getAsMention() + " to the whitelist with the username " + e.getSplitArgs()[0]);
        new Thread(() -> ATLauncherUtils.addPlayer(e.getSplitArgs()[0])).start();
      } catch (SQLException e1) {
        e.reply("Error while adding you to the whitelist...");
      }
    }
  }
}
