package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import io.github.jroy.cowbot.utils.Constants;
import io.github.jroy.cowbot.utils.JaroWinklerDistance;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;

import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinDateCommand extends CommandBase {

  private static final Pattern MENTION_REGEX = Pattern.compile("<@!?(\\d+)>");

  private final HashMap<String, OffsetDateTime> joinDateCache = new HashMap<>();

  public JoinDateCommand() {
    super("joindate", "[<user mention>]", "What's your join date??");
  }

  @Override
  protected void executeCommand(CommandEvent event) {
    if (!event.isOwner()) {
      event.getMessage().addReaction("❌").queue();
      return;
    }

    Member target = matchMember(event.getMember(), event.getArgs());

    Message message = event.getChannel().sendMessage("Searching through the jungle...").complete();

    OffsetDateTime joinDate = joinDateCache.getOrDefault(target.getUser().getId(), null);

    if (joinDate == null) {
      MessageHistory history = Objects.requireNonNull(Objects.requireNonNull(event.getJDA().getGuildById(Constants.GUILD_ID)).getTextChannelById(Constants.LOG_CHANNEL_ID)).getHistory();

      OffsetDateTime latestJoinDate = null;

      while (joinDate == null) {
        List<Message> currentMessages = history.retrievePast(100).complete();
        if (currentMessages.isEmpty()) {
          joinDate = latestJoinDate != null ? latestJoinDate : target.getTimeJoined();
        }

        for (Message curMsg : currentMessages) {
          if (curMsg.getAuthor().getId().equalsIgnoreCase(target.getUser().getId())) {
            latestJoinDate = curMsg.getTimeCreated();
          }
        }
      }

      joinDateCache.put(target.getUser().getId(), joinDate);
    }

    message.editMessage(target.getUser().getName() + "#" + target.getUser().getDiscriminator() + "'s join date is " + joinDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + joinDate.getDayOfMonth() + " " + joinDate.getYear()).queue();
  }

  private Member matchMember(Member def, String string) {
    Guild guild = def.getGuild();
    Matcher matcher = MENTION_REGEX.matcher(string);
    if (matcher.matches()) {
      return guild.getMemberById(matcher.group(1));
    }

    Member closest = null;
    // minimum similarity = 0.5 - higher requires more similarity
    double distance = 0.5;

    for (Member member : guild.getMembers()) {
      double nicknameDistance = JaroWinklerDistance.apply(string, member.getEffectiveName());
      if (nicknameDistance > distance) {
        closest = member;
        distance = nicknameDistance;
      }
      double usernameDistance = JaroWinklerDistance.apply(string, member.getUser().getName());
      if (usernameDistance > distance) {
        closest = member;
        distance = usernameDistance;
      }
      double fullNameDistance = JaroWinklerDistance.apply(string, member.getUser().getName() + "#" + member.getUser().getDiscriminator());
      if (fullNameDistance > distance) {
        closest = member;
        distance = fullNameDistance;
      }
    }
    return Objects.requireNonNullElse(closest, def);
  }
}
