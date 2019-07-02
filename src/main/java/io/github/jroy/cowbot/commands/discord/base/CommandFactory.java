package io.github.jroy.cowbot.commands.discord.base;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import io.github.jroy.cowbot.utils.Constants;
import net.dv8tion.jda.api.entities.Activity;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

  private CommandClientBuilder clientBuilder;

  private Map<String, CommandBase> registeredCommands = new HashMap<>();

  public CommandFactory(String prefix, String alternativePrefix) {
    clientBuilder = new CommandClientBuilder();
    clientBuilder.setPrefix(prefix);
    clientBuilder.setAlternativePrefix(alternativePrefix);
    clientBuilder.setOwnerId(Constants.OWNER_ID);
    clientBuilder.setCoOwnerIds(Constants.CO_OWNER_IDS);
    clientBuilder.useHelpBuilder(false);
    clientBuilder.setActivity(Activity.streaming("your bytes", "https://www.twitch.tv/jschlatt"));
  }

  public CommandFactory addCommands(CommandBase... commands) {
    clientBuilder.addCommands(commands);
    for (CommandBase base : commands) {
      registeredCommands.put(base.getName(), base);
    }
    return this;
  }

  public CommandClient build() {
    return clientBuilder.build();
  }

  public Map<String, CommandBase> getRegisteredCommands() {
    return registeredCommands;
  }
}
