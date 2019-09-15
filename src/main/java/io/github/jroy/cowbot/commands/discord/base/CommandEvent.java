package io.github.jroy.cowbot.commands.discord.base;

import com.jagrosh.jdautilities.command.CommandClient;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A custom implementation of JDA-Utilities's {@link com.jagrosh.jdautilities.command.CommandEvent CommandEvent} class that makes our use-case easier.
 */
public class CommandEvent extends com.jagrosh.jdautilities.command.CommandEvent {

  CommandEvent(MessageReceivedEvent event, String args, CommandClient client) {
    super(event, args, client);
  }

  public Member getMentionedMember() {
    if (containsMention()) {
      return getMessage().getMentionedMembers().get(0);
    }
    return null;
  }

  public boolean containsMention() {
    return getMessage().getMentionedUsers().size() >= 1;
  }

  /**
   * Wrapper for getting amount of mentioned users in a message.
   *
   * @return The amount of mentioned users.
   */
  public int getMentionsAmount() {
    return getMessage().getMentionedMembers().size();
  }

  /**
   * @return Gets the arguments of the command split by spaces.
   */
  public String[] getSplitArgs() {
    return getArgs().split(" ");
  }
}