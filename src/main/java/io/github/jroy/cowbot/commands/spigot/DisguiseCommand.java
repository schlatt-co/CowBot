package io.github.jroy.cowbot.commands.spigot;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.jroy.cowbot.managers.spigot.ChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisguiseCommand implements CommandExecutor {

  private ChatManager chatManager;

  public DisguiseCommand(ChatManager chatManager) {
    this.chatManager = chatManager;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    Player player = (Player) sender;
    if (sender.hasPermission("trevor.admin")) {
      if (chatManager.disCache.containsKey(player.getUniqueId().toString())) {
        chatManager.disCache.remove(player.getUniqueId().toString());
        sender.sendMessage("undis");
        PlayerProfile profile = player.getPlayerProfile();
        profile.setName(player.getName());
        player.setPlayerProfile(profile);
        return true;
      }
      chatManager.disCache.put(player.getUniqueId().toString(), args[0]);
      sender.sendMessage("ye");
      PlayerProfile profile = player.getPlayerProfile();
      profile.setName(args[0]);
      player.setPlayerProfile(profile);
    }
    return true;
  }
}
