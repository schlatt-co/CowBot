package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;

public class NameCommand extends CommandBase {

  private ProxiedCow cow;

  public NameCommand(ProxiedCow cow) {
    super("mcname", "<user mention>", "Gets the mc name of a discord user.", true);
    this.cow = cow;
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
      if (!cow.getDatabaseFactory().isLinked(member.getUser().getId())) {
        builder.append(member.getAsMention()).append(": User does not have a linked account\n");
        continue;
      }
      try {
        builder.append(member.getAsMention()).append(": ").append(cow.getDatabaseFactory().getUsernameFromDiscordId(member.getUser().getId())).append("\n");
      } catch (SQLException ex) {
        builder.append(member.getAsMention()).append(": User is linked but I could not fetch their username\n");
      }
    }
    e.reply(builder.toString());
  }
}
