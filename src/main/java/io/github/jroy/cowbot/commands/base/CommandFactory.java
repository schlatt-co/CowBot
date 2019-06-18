package io.github.jroy.cowbot.commands.base;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import io.github.jroy.cowbot.utils.Logger;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

  private CommandClientBuilder clientBuilder;

  private Map<String, CommandBase> registeredCommands = new HashMap<>();

  public CommandFactory(String prefix, String alternativePrefix) {
    Logger.log("Loading Command Factory...");
    clientBuilder = new CommandClientBuilder();
    clientBuilder.setPrefix(prefix);
    clientBuilder.setAlternativePrefix(alternativePrefix);
    clientBuilder.setOwnerId("194473148161327104");
    clientBuilder.setCoOwnerIds("102546170877853696", "227600936061763604");
    clientBuilder.useHelpBuilder(false);
  }

  public void addCommands(CommandBase... commands) {
    Logger.log("Adding Commands...");
    clientBuilder.addCommands(commands);
    for (CommandBase base : commands) {
      registeredCommands.put(base.getName(), base);
    }
    Logger.log("Added " + commands.length + " Commands!");
  }

  public CommandClient build() {
    return clientBuilder.build();
  }

  public Map<String, CommandBase> getRegisteredCommands() {
    return registeredCommands;
  }
}
