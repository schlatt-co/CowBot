package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import io.github.jroy.cowbot.managers.proxy.discord.Roles;
import net.dv8tion.jda.api.entities.Role;

public class PingCommand extends CommandBase {

  public PingCommand() {
    super("ping", "<events/main>", "Pings the MC Events role");
  }

  @Override
  protected void executeCommand(CommandEvent event) {
    if (!event.isOwner()) {
      event.getMessage().addReaction("❌").queue();
      return;
    }
    if (event.getSplitArgs().length < 1 || (!event.getSplitArgs()[0].equals("main") && !event.getSplitArgs()[0].equals("events"))) {
      event.replyError(invalid);
      return;
    }

    Role role;
    if (event.getSplitArgs()[0].equals("main")) {
      role = Roles.MINECRAFT.getRole(event.getGuild());
    } else {
      role = Roles.MC_EVENTS.getRole(event.getGuild());
    }
    role.getManager().setMentionable(true).complete();
    event.getChannel().sendMessage(role.getAsMention()).complete();
    role.getManager().setMentionable(false).complete();
  }
}
