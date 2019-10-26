package io.github.jroy.cowbot.managers.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.commands.spigot.ChatCommand;
import io.github.jroy.cowbot.commands.spigot.DirtCommand;
import io.github.jroy.cowbot.commands.spigot.JoinDateCommand;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.utils.AsyncFinishedChatEvent;
import io.github.jroy.cowbot.utils.ChatEnum;
import io.github.jroy.tagger.events.TaggerTagUpdateEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager extends SpigotModule {

  private CowBot cowBot;

  private Map<UUID, String> prefixes = new HashMap<>();
  Map<String, ChatEnum> chatEnumCache = new HashMap<>();

  private boolean silence = false;

  public ChatManager(CowBot cowBot) {
    super("Chat Manager", cowBot);
    this.cowBot = cowBot;
  }

  @Override
  public void addCommands() {
    addCommand("chat", new ChatCommand(this));
    addCommand("joindate", new JoinDateCommand());
    addCommand("dirt", new DirtCommand());
  }

  @EventHandler
  public void onTaggerUpdate(TaggerTagUpdateEvent event) {
    prefixes.put(event.getPlayerUuid(), event.getTag().getText());
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

    ChatEnum chatEnum = chatEnumCache.getOrDefault(event.getPlayer().getName(), ChatEnum.UNKNOWN);
    boolean hasChatEnum = chatEnum != null && chatEnum != ChatEnum.UNKNOWN;
    String name = (hasChatEnum ? ChatColor.stripColor(event.getPlayer().getDisplayName()) : event.getPlayer().getDisplayName());

    String prefix = prefixes.getOrDefault(event.getPlayer().getUniqueId(), "");
    prefix = prefix.equals("") ? prefix : (prefix + " ");
    event.setFormat(prefix + ChatColor.GRAY + "<" + (hasChatEnum ? chatEnum.getChatColor() : "") + name + ChatColor.GRAY + "> " + ChatColor.WHITE + event.getMessage().replaceAll("(?:[^%]|^)(?:(%%)+|)(%)(?:[^%])\n", "%%").replaceAll("%", "%%"));
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
