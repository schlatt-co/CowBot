package io.github.jroy.cowbot.commands;

import io.github.jroy.cowbot.ProxiedCow;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;

public class TrevorCommand extends Command {

  private ProxiedCow cowBot;
  private JDA jda;

  public TrevorCommand(ProxiedCow cowBot, JDA jda) {
    super("trevor");
    this.cowBot = cowBot;
    this.jda = jda;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (sender.hasPermission("cowbot.trevor")) {
      if (args.length < 2) {
        sender.sendMessage(new TextComponent("/trevor whois <player>"));
        return;
      }

      switch (args[0].toLowerCase()) {
        case "whois": {
          if (!cowBot.databaseFactory.isWhitelisted(args[1])) {
            sender.sendMessage(new ComponentBuilder("Unknown Player!").color(ChatColor.RED).create());
            return;
          }

          try {
            String discordId = cowBot.databaseFactory.getDiscordIdFromUsername(args[1]);
            User user = jda.getUserById(discordId);
            sender.sendMessage(new TextComponent(ChatColor.YELLOW + "Target User is " + ChatColor.GOLD + "@" + user.getName() + "#" + user.getDiscriminator() + ChatColor.YELLOW + " with user id " + ChatColor.GOLD + user.getId()));
          } catch (SQLException e) {
            sender.sendMessage(new ComponentBuilder("Error while fetching user data: " + e.getMessage()).color(ChatColor.RED).create());
            e.printStackTrace();
          }
          break;
        }
        case "motd": {
          StringBuilder motdBuilder = new StringBuilder();
          for (String arg : args) {
            motdBuilder.append(arg).append(" ");
          }
          ProxiedCow.serverMotd = motdBuilder.toString().replaceFirst("motd ", "").replace("\\n", "\n");
          sender.sendMessage(new TextComponent("Updated MOTD!"));
          break;
        }
        default: {
          sender.sendMessage(new ComponentBuilder("Invalid Argument!").color(ChatColor.RED).create());
          break;
        }
      }
    } else {
      sender.sendMessage(new ComponentBuilder("You're not an admin commie!").color(ChatColor.RED).create());
    }
  }
}
