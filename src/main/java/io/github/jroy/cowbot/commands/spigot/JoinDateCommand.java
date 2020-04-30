package io.github.jroy.cowbot.commands.spigot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.Locale;

public class JoinDateCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull  String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("No");
      return true;
    }

    OfflinePlayer target = (Player) sender;

    if (args.length == 1 && sender.hasPermission("trevor.mod")) {
      //noinspection deprecation
      target = Bukkit.getOfflinePlayer(args[0]);
      if (!target.hasPlayedBefore()) {
        sender.sendMessage("User has not played before (or joined today)");
        return true;
      }
    }

    OffsetDateTime joinDate = Instant.ofEpochMilli(target.getFirstPlayed()).atOffset(ZoneOffset.UTC);
    sender.sendMessage(ChatColor.YELLOW + "Your join date is " + joinDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + joinDate.getDayOfMonth() + " " + joinDate.getYear());
    return true;
  }
}
