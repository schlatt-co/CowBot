package io.github.jroy.cowbot.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.CowBot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ServerShutdownCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender.isOp()) {
      ByteArrayDataOutput out1 = ByteStreams.newDataOutput();
      out1.writeUTF("target");
      out1.writeUTF("holding");
      sender.getServer().sendPluginMessage(CowBot.instance, "trevor:main", out1.toByteArray());
      for (Player player : CowBot.instance.getServer().getOnlinePlayers()) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player.getName());
        out.writeUTF("holding");
        player.sendPluginMessage(CowBot.instance, "BungeeCord", out.toByteArray());
      }
      CowBot.instance.getServer().getScheduler().runTaskLater(CowBot.instance, () -> CowBot.instance.getServer().shutdown(), 20);
      return true;
    }
    return false;
  }
}
