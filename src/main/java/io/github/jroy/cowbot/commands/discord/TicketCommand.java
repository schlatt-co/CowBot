package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import io.github.jroy.cowbot.managers.proxy.TicketManager;

import java.sql.SQLException;

public class TicketCommand extends CommandBase {

  private final TicketManager ticketManager;

  public TicketCommand(TicketManager ticketManager) {
    super("ticket", "<new/close> <reason>", "Opens or closes a ticket");
    this.ticketManager = ticketManager;
  }

  @Override
  protected void executeCommand(CommandEvent event) {
    if (event.getArgs().startsWith("new ")) {
      try {
        ticketManager.spawnTicketChannel(event.getMember(), event.getArgs().replaceFirst("new ", ""));
      } catch (SQLException e) {
        event.reply("jesus fuckin christ");
        e.printStackTrace();
      }
    } else if (event.getArgs().startsWith("close") && event.isOwner() && event.getChannel().getName().startsWith("ticket")) {
      event.getTextChannel().delete().queue();
    } else {
      event.reply(invalid);
    }
  }
}
