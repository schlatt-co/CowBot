package io.github.jroy.cowbot.commands.spigot;

import org.bukkit.ChatColor;
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

    Player player = (Player) sender;

    OffsetDateTime joinDate = Instant.ofEpochMilli(player.getFirstPlayed()).atOffset(ZoneOffset.UTC);

    player.sendMessage(ChatColor.YELLOW + "Your join date is " + joinDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + joinDate.getDayOfMonth() + " " + joinDate.getYear());

    return true;
  }
}
