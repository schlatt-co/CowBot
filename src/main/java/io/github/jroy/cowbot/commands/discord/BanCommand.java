package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import io.github.jroy.cowbot.managers.proxy.DiscordManager;
import io.github.jroy.cowbot.managers.proxy.discord.Roles;
import io.github.jroy.cowbot.utils.Constants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;
import java.util.Objects;

public class BanCommand extends CommandBase {

  private DiscordManager discordManager;

  public BanCommand(DiscordManager discordManager) {
    super("ban", "<id> <reason>", "Bans a user from linking their account.", true);
    this.discordManager = discordManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (e.getArgs().isEmpty()) {
      e.replyError(invalid);
      return;
    }

    if (e.getSplitArgs().length == 1) {
      if (discordManager.getDatabaseManager().isBanned(e.getSplitArgs()[0])) {
        try {
          discordManager.getDatabaseManager().pardonUser(e.getSplitArgs()[0]);
          e.reply("Pardoned User!");
        } catch (SQLException ex) {
          e.replyError("Error while pardoning user: " + ex.getMessage());
        }
      } else {
        e.replyError("Please supply a reason to ban the user!");
      }
    } else {
      if (discordManager.getDatabaseManager().isBanned(e.getSplitArgs()[0])) {
        try {
          discordManager.getDatabaseManager().pardonUser(e.getSplitArgs()[0]);
          e.reply("Pardoned User!");
        } catch (SQLException ex) {
          e.replyError("Error while pardoning user: " + ex.getMessage());
        }
        return;
      }

      try {
        discordManager.getDatabaseManager().banUser(e.getSplitArgs()[0], e.getArgs().replace(e.getSplitArgs()[0] + " ", ""));
        Guild guild =  Objects.requireNonNull(e.getJDA().getGuildById(Constants.GUILD_ID));
        Member member = guild.getMemberById(e.getSplitArgs()[0]);
        if (member != null && member.getRoles().stream().anyMatch(p -> p.getId().equals(Roles.MINECRAFT.getRoleId()))) {
          guild.removeRoleFromMember(member, Roles.MINECRAFT.getRole(guild)).queue();
        }
        e.reply("Banned User!");
      } catch (SQLException ex) {
        e.replyError("Error while banning user: " + ex.getMessage());
      }
    }
  }
}
