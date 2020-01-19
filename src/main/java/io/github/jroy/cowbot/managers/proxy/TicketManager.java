package io.github.jroy.cowbot.managers.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.managers.base.ProxyModule;
import io.github.jroy.cowbot.utils.Constants;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.SQLException;
import java.util.Objects;

public class TicketManager extends ProxyModule {

  private final DiscordManager discordManager;

  private int currentId = 1;

  public TicketManager(ProxiedCow plugin, DiscordManager discordManager) {
    super("Ticket Manager", plugin);
    this.discordManager = discordManager;
  }

  public void spawnTicketChannel(Member member, String reason) throws SQLException {
    Guild guild = Objects.requireNonNull(discordManager.getJda().getGuildById(Constants.GUILD_ID));
    TextChannel channel = Objects.requireNonNull(guild.getCategoryById(Constants.TICKET_CATEGORY_ID))
        .createTextChannel("ticket-" + currentId)
        .setTopic("Created by " + member.getAsMention() + " with reason: " + reason)
        .addPermissionOverride(Objects.requireNonNull(guild.getRoleById("594362773266366475")), Permission.VIEW_CHANNEL.getRawValue(), 0)
        .addPermissionOverride(member, Permission.VIEW_CHANNEL.getRawValue(), 0)
        .complete();
    currentId++;
    channel.sendMessage("Created new ticket for " + member.getAsMention() + "! Their username is " + discordManager.getDatabaseManager().getUsernameFromDiscordId(member.getId())
    + "\nPlease make sure to include as much information as possible to allow admins to assist as fast as possible.");
  }
}
