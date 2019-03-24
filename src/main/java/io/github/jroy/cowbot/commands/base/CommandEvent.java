package io.github.jroy.cowbot.commands.base;

import com.jagrosh.jdautilities.command.CommandClient;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * A custom implementation of JDA-Utilities's {@link com.jagrosh.jdautilities.command.CommandEvent CommandEvent} class that makes our use-case easier.
 */
public class CommandEvent extends com.jagrosh.jdautilities.command.CommandEvent {

  public CommandEvent(MessageReceivedEvent event, String args, CommandClient client) {
    super(event, args, client);
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
