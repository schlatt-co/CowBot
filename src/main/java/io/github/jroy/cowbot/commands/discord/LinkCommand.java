package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import io.github.jroy.cowbot.managers.proxy.DiscordManager;
import io.github.jroy.cowbot.utils.Constants;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.SQLException;
import java.time.Instant;

public class LinkCommand extends CommandBase {

  private DiscordManager discordManager;

  private boolean disableLinking = false;

  public LinkCommand(DiscordManager discordManager) {
    super("link", "<minecraft username>", "Links your minecraft username with your discord account.");
    this.discordManager = discordManager;
    setDisabled(true);
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.getTextChannel().getId().equalsIgnoreCase(Constants.MC_CHANNEL_ID)) {
      return;
    }

    if (disableLinking && !e.isOwner()) {
      e.replyError("Link requests are currently disabled, please try again later!");
      return;
    }

    if ((Instant.now().getEpochSecond() - e.getMember().getTimeCreated().toEpochSecond()) <= 432000) {
      e.replyError(e.getMember().getAsMention() + ": To prevent abuse, your linkage request was blocked. Please try again in a few days.");
      //noinspection ConstantConditions
      e.getGuild().getTextChannelById(Constants.SHOUT_CHANNEL_ID).sendMessage("New User Warning: " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator()).queue();
      return;
    }

    if (e.getMember().getRoles().isEmpty() || discordManager.getDatabaseManager().hasNoPaymentRole(e.getMember().getRoles())) {
      e.reply(e.getMember().getAsMention() + ": The minecraft server is for profileable users only! Pay Schlatt or no dice.");
      return;
    }

    if (e.getArgs().isEmpty()) {
      e.reply(invalid);
      return;
    }

    if (e.getSplitArgs()[0].equalsIgnoreCase("toggle") && e.isOwner()) {
      disableLinking = !disableLinking;
      e.reply("Toggled disabled state to: " + disableLinking);
      return;
    }

    String userId = e.getMember().getUser().getId();
    if (discordManager.getDatabaseManager().isBanned(userId)) {
      try {
        //noinspection ConstantConditions
        e.getGuild().getTextChannelById(Constants.SHOUT_CHANNEL_ID).sendMessage("Banned user (" + discordManager.getDatabaseManager().getBanReason(userId) + ") attempted to link: " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator()).queue();
        e.reply("Added " + e.getMember().getAsMention() + " to the whitelist with the username " + e.getSplitArgs()[0]);
      } catch (SQLException ex) {
        e.replyError("You are banned from the server but I honestly don't give two shits why.");
      }
      return;
    }

    if (discordManager.getDatabaseManager().isLinked(userId)) {
      try {
        String pastName = discordManager.getDatabaseManager().getUsernameFromDiscordId(userId);
        discordManager.getDatabaseManager().updateUser(userId, e.getSplitArgs()[0]);
        e.reply("Updated " + e.getMember().getAsMention() + "'s current Minecraft name to " + e.getSplitArgs()[0]);
        if (ProxyServer.getInstance().getPlayer(pastName) != null) {
          ProxyServer.getInstance().getPlayer(pastName).disconnect(new TextComponent("Your username has been updated!\nTo prevent freeloading, you've been disconnected\nfuck you"));
        }
      } catch (SQLException ex) {
        e.replyError("Error while updating your Minecraft name: " + ex.getMessage());
        ex.printStackTrace();
      }
      return;
    }
    try {
      discordManager.getDatabaseManager().linkUser(userId, e.getSplitArgs()[0]);
      e.reply("Added " + e.getMember().getAsMention() + " to the whitelist with the username " + e.getSplitArgs()[0]);
    } catch (SQLException ex) {
      e.replyError("Error white linking your Minecraft name: " + ex.getMessage());
      ex.printStackTrace();
    }

  }
}
