package io.github.jroy.cowbot.commands.spigot;

import io.github.jroy.cowbot.managers.spigot.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ChatCommand implements CommandExecutor {

  private final ChatManager chatManager;

  public ChatCommand(ChatManager chatManager) {
    this.chatManager = chatManager;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender.hasPermission("trevor.admin")) {
      if (args.length == 0) {
        sender.sendMessage(ChatColor.AQUA + "Chat>> " + ChatColor.YELLOW + "/chat clear");
        sender.sendMessage(ChatColor.AQUA + "Chat>> " + ChatColor.YELLOW + "/chat silence");
        return true;
      }
      switch (args[0]) {
        case "clear": {
          for (int i = 0; i < 200; ++i) {
            Bukkit.broadcastMessage(" ");
          }
          Bukkit.broadcastMessage(ChatColor.AQUA + "Chat>> " + ChatColor.YELLOW + "Cleared Chat!");
          break;
        }
        case "silence": {
          chatManager.setSilence(!chatManager.isSilence());
          Bukkit.broadcastMessage(ChatColor.AQUA + "Chat>> " + ChatColor.YELLOW + "The chat is " + (chatManager.isSilence() ? "now" : "no longer") + " silenced!");
          break;
        }
        default: {
          sender.sendMessage(ChatColor.AQUA + "Chat>> " + ChatColor.YELLOW + "Unknown sub-command!");
          break;
        }
      }
      return true;
    }
    sender.sendMessage("No permission!");
    return true;
  }
}
