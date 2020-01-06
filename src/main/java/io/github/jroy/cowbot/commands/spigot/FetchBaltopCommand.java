package io.github.jroy.cowbot.commands.spigot;

import io.github.jroy.cowbot.utils.FileCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class FetchBaltopCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender instanceof Player) {
      return false;
    }
    Bukkit.dispatchCommand(new FileCommandSender(Bukkit.getConsoleSender(), Path.of(Bukkit.getWorldContainer().toString(), "/baltop/baltopone.txt")), "baltop");
    Bukkit.dispatchCommand(new FileCommandSender(Bukkit.getConsoleSender(), Path.of(Bukkit.getWorldContainer().toString(), "/baltop/baltoptwo.txt")), "baltop 2");
    Bukkit.dispatchCommand(new FileCommandSender(Bukkit.getConsoleSender(), Path.of(Bukkit.getWorldContainer().toString(), "/baltop/baltopthree.txt")), "baltop 3");
    return true;
  }
}
