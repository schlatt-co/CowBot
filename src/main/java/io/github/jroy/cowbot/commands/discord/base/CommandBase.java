package io.github.jroy.cowbot.commands.discord.base;

import com.jagrosh.jdautilities.command.Command;
import io.github.jroy.cowbot.utils.Constants;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/**
 * A custom implementation of JDA-Utilities's {@link com.jagrosh.jdautilities.command.Command Command} class that makes our use-case easier.
 */
public abstract class CommandBase extends Command {
  /**
   * Command usage
   */
  protected final String invalid;

  /**
   * The command may not be used when true
   * Devs+ can bypass this
   */
  private boolean disabled = false;

  /**
   * Constructor for commands with certain role requires for execution.
   *
   * @param commandName Command's name to be used for execution.
   * @param arguments   Arguments of the command to be used for command help.
   * @param helpMessage Command description to be used inside the command list.
   */
  public CommandBase(@NotNull String commandName, String arguments, String helpMessage) {
    this(commandName, arguments, helpMessage, false);
  }

  /**
   * Constructor for commands with certain role requires for execution.
   *
   * @param commandName Command's name to be used for execution.
   * @param arguments   Arguments of the command to be used for command help.
   * @param helpMessage Command description to be used inside the command list.
   * @param ownerOnly   Is the command only executable for the owner.
   */
  public CommandBase(@NotNull String commandName, String arguments, String helpMessage, boolean ownerOnly) {
    this.name = commandName;
    this.arguments = arguments;
    this.help = helpMessage;
    this.ownerCommand = ownerOnly;
    this.invalid = "**Correct Usage:**" + " ^" + name + " " + arguments;
  }

  /**
   * Sets the disabled state of a command.
   *
   * @param disabled Disabled state.
   */
  @SuppressWarnings("WeakerAccess")
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  /**
   * Gets the disabled state of the command.
   *
   * @return Disabled state.
   */
  public boolean isDisabled() {
    return disabled;
  }

  @Override
  protected final void execute(com.jagrosh.jdautilities.command.CommandEvent event) {
    Member member = event.getMember();

    if (disabled && !event.getGuild().getId().equalsIgnoreCase(Constants.GUILD_ID)) {
      event.getMessage().addReaction("❌").queue();
      return;
    }
    executeCommand(new io.github.jroy.cowbot.commands.discord.base.CommandEvent(event.getEvent(), event.getArgs(), event.getClient()));
  }

  /**
   * Triggered when the command is ran and the user has the required permission.
   *
   * @param event The information associated with the command calling
   */
  protected abstract void executeCommand(io.github.jroy.cowbot.commands.discord.base.CommandEvent event);
}