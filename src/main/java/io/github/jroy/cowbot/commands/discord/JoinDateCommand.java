package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import net.dv8tion.jda.api.entities.Member;

import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class JoinDateCommand extends CommandBase {

  public JoinDateCommand() {
    super("joindate", "[<user mention>]", "What's your join date??");
  }

  @Override
  protected void executeCommand(CommandEvent event) {
    Member target = event.getMember();
    if (event.containsMention()) {
      target = event.getMentionedMember();
    }

    OffsetDateTime joinDate = target.getTimeJoined();
    event.reply(target.getAsMention() + "'s join date is " + joinDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + joinDate.getDayOfMonth() + " " + joinDate.getYear());
  }
}
