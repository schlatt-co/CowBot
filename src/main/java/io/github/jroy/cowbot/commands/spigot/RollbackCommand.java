package io.github.jroy.cowbot.commands.spigot;

import io.github.jroy.cowbot.managers.spigot.ChatManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollbackCommand implements CommandExecutor {

  private final ChatManager chatManager;
  private final static Pattern pattern = Pattern.compile("https://github.com/schlattsubserver/inventories/raw/\\b[0-9a-f]{5,40}\\b/([a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8})\\.dat");

  public RollbackCommand(ChatManager chatManager) {
    this.chatManager = chatManager;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (!sender.hasPermission("trevor.rollback") || args.length == 0) {
      return false;
    }

    String urlString = args[0];
    Matcher matcher = pattern.matcher(urlString);
    if (!matcher.matches()) {
      sender.sendMessage(ChatColor.RED + "Invalid URL!");
      return true;
    }

    UUID targetUUID = UUID.fromString(matcher.group(1));
    try {
      chatManager.addRollbackQueue(targetUUID);
      Player targetPlayer = Bukkit.getPlayer(targetUUID);
      if (targetPlayer != null && targetPlayer.isOnline()) {
        targetPlayer.kickPlayer("Starting rollback on you, please rejoin in 25 seconds!");
      }
      URL url = new URL(urlString);
      new Thread(() -> {
        try {
          sender.sendMessage(ChatColor.YELLOW + "Waiting five seconds to ensure playerdata invalidation...");
          Thread.sleep(5000);
          File playerData = new File(new File(Objects.requireNonNull(Bukkit.getWorld("world")).getWorldFolder(), "playerdata"), targetUUID.toString() + ".dat");
          if (playerData.exists()) {
            playerData.delete();
          }
          sender.sendMessage(ChatColor.GOLD + "Download of player data...");
          FileUtils.copyURLToFile(
              url,
              playerData,
              5 * 1000, //5 sec
              10 * 1000 //10 sec
          );
          chatManager.removeRollbackQueue(targetUUID);
          sender.sendMessage(ChatColor.GREEN + "Player data rolled back successfully!");
        } catch (Exception e) {
          sender.sendMessage(ChatColor.RED + "Error during I/O Thread: " + e.getMessage());
          chatManager.removeRollbackQueue(targetUUID);
          e.printStackTrace();
        }
      }).start();
    } catch (Exception e) {
      sender.sendMessage(ChatColor.RED + "Error while rolling back inventory: " + e.getMessage());
      chatManager.removeRollbackQueue(targetUUID);
      e.printStackTrace();
    }
    return true;
  }
}
