package io.github.jroy.cowbot.managers.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.commands.spigot.ChatCommand;
import io.github.jroy.cowbot.commands.spigot.DirtCommand;
import io.github.jroy.cowbot.commands.spigot.DisguiseCommand;
import io.github.jroy.cowbot.commands.spigot.JoinDateCommand;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.utils.AsyncFinishedChatEvent;
import io.github.jroy.cowbot.utils.ChatEnum;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class ChatManager extends SpigotModule {

  private CowBot cowBot;

  Map<String, ChatEnum> chatEnumCache = new HashMap<>();
  public Map<String, String> disCache = new HashMap<>();

  private boolean silence = false;

  public ChatManager(CowBot cowBot) {
    super("Chat Manager", cowBot);
    this.cowBot = cowBot;
  }

  @Override
  public void addCommands() {
    addCommand("disguise", new DisguiseCommand(this));
    addCommand("chat", new ChatCommand(this));
    addCommand("joindate", new JoinDateCommand());
    addCommand("dirt", new DirtCommand());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChat(AsyncPlayerChatEvent event) {
    if (event.getPlayer().hasPermission("trevor.admin") && event.getMessage().equalsIgnoreCase("hey trevor can you purge the normie cache")) {
      chatEnumCache.clear();
      cowBot.getServer().getScheduler().runTaskLaterAsynchronously(cowBot, () -> cowBot.getServer().broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.WHITE + "sure dad :)"), 10);
    }

    if (silence && !event.getPlayer().hasPermission("trevor.mod")) {
      event.getPlayer().sendMessage(org.bukkit.ChatColor.AQUA + "Chat>> " + org.bukkit.ChatColor.YELLOW + "That chat is silenced!");
      event.setCancelled(true);
      return;
    }


    String prefix = "";
    if (!disCache.containsKey(event.getPlayer().getUniqueId().toString())) {
      if (event.getPlayer().hasPermission("trevor.admin")) {
        prefix = ChatColor.GRAY + "[" + ChatColor.RED + "Admin" + ChatColor.GRAY + "] ";
      } else if (event.getPlayer().hasPermission("trevor.mod")) {
        prefix = ChatColor.GRAY + "[" + ChatColor.GREEN + "Mod" + ChatColor.GRAY + "] ";
      } else if (event.getPlayer().hasPermission("trevor.stonks")) {
        prefix = ChatColor.GRAY + "[" + ChatColor.GREEN + "$" + ChatColor.GRAY + "] ";
      } else if (event.getPlayer().hasPermission("trevor.donor")) {
        prefix = ChatColor.GRAY + "[" + ChatColor.AQUA + "Donor" + ChatColor.GRAY + "] ";
      } else if (event.getPlayer().hasPermission("trevor.redacted")) {
        prefix = ChatColor.GRAY + "[" + ChatColor.RED + "R" + ChatColor.GOLD + "E" + ChatColor.YELLOW + "D" + ChatColor.GREEN + "A" + ChatColor.BLUE + "C" + ChatColor.LIGHT_PURPLE + "T" + ChatColor.RED + "E" + ChatColor.GOLD + "D" + ChatColor.GRAY + "] ";
      } else if (event.getPlayer().hasPermission("trevor.twitch")) {
        prefix = ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.GRAY + "] ";
      } else if (event.getPlayer().hasPermission("trevor.content")) {
        prefix = ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "Content" + ChatColor.GRAY + "] ";
      }
    }

    ChatEnum chatEnum = chatEnumCache.getOrDefault(event.getPlayer().getName(), ChatEnum.UNKNOWN);
    boolean hasChatEnum = chatEnum != null && chatEnum != ChatEnum.UNKNOWN;
    String name = (hasChatEnum ? ChatColor.stripColor(event.getPlayer().getDisplayName()) : event.getPlayer().getDisplayName());
    if (disCache.containsKey(event.getPlayer().getName())) {
      name = disCache.get(event.getPlayer().getName());
    }

    event.setFormat(prefix + ChatColor.GRAY + "<" + (hasChatEnum ? chatEnum.getChatColor() : "") + name + ChatColor.GRAY + "> " + ChatColor.WHITE + event.getMessage().replaceAll("(?:[^%]|^)(?:(%%)+|)(%)(?:[^%])\n", "%%").replaceAll("%","%%"));
    cowBot.getServer().getPluginManager().callEvent(new AsyncFinishedChatEvent(prefix, event.getPlayer().getDisplayName(), event.getMessage()));
  }

  @SuppressWarnings("UnstableApiUsage")
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event) {
    if (!chatEnumCache.containsKey(event.getPlayer().getName()) || chatEnumCache.get(event.getPlayer().getName()).equals(ChatEnum.UNKNOWN)) {
      chatEnumCache.put(event.getPlayer().getName(), ChatEnum.UNKNOWN);
      Bukkit.getScheduler().runTaskLaterAsynchronously(cowBot, () -> {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("trevorrequest|" + cowBot.getCurrentServer().getServerName());
        out.writeUTF(event.getPlayer().getName());
        event.getPlayer().sendPluginMessage(cowBot, "trevor:main", out.toByteArray());
      }, 30);
    }
  }

  public boolean isSilence() {
    return silence;
  }

  public void setSilence(boolean silence) {
    this.silence = silence;
  }
}
