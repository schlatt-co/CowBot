package io.github.jroy.cowbot.commands;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.base.CommandBase;
import io.github.jroy.cowbot.commands.base.CommandEvent;
import net.dv8tion.jda.core.entities.Member;

import java.sql.SQLException;

public class NameCommand extends CommandBase {

  private ProxiedCow proxiedCow;

  public NameCommand(ProxiedCow proxiedCow) {
    super("mcname", "<user mention>", "Gets the mc name of a discord user.", true);
    this.proxiedCow = proxiedCow;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getMessage().getMentionedMembers().size() == 0) {
      e.replyError(help);
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append("MC Link Status:\n");
    for (Member member : e.getMessage().getMentionedMembers()) {
      if (!proxiedCow.databaseFactory.isLinked(member.getUser().getId())) {
        builder.append(member.getAsMention()).append(": User does not have a linked account\n");
        continue;
      }
      try {
        builder.append(member.getAsMention()).append(": ").append(proxiedCow.databaseFactory.getUsernameFromDiscordId(member.getUser().getId())).append("\n");
      } catch (SQLException ex) {
        builder.append(member.getAsMention()).append(": User is linked but I could not fetch their username\n");
      }
    }
    e.reply(builder.toString());
  }
}
