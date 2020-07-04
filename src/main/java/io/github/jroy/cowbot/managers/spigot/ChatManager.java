package io.github.jroy.cowbot.managers.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.commands.spigot.ChatCommand;
import io.github.jroy.cowbot.commands.spigot.DirtCommand;
import io.github.jroy.cowbot.commands.spigot.JoinDateCommand;
import io.github.jroy.cowbot.commands.spigot.RollbackCommand;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.utils.AsyncFinishedChatEvent;
import io.github.jroy.cowbot.utils.ChatEnum;
import io.github.jroy.tagger.events.TaggerTagUpdateEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatManager extends SpigotModule {

  private final CowBot cowBot;

  private final Map<UUID, String> prefixes = new HashMap<>();
  protected final Map<String, ChatEnum> chatEnumCache = new HashMap<>();
  private final ConcurrentLinkedQueue<UUID> rollbackQueue = new ConcurrentLinkedQueue<>();

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
    addCommand("rollback", new RollbackCommand(this));
  }

  @EventHandler
  public void onTaggerUpdate(TaggerTagUpdateEvent event) {
    prefixes.put(event.getPlayerUuid(), event.getTag().getText());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChat(AsyncPlayerChatEvent event) {
    if (!chatEnumCache.containsKey(event.getPlayer().getName())) {
      Bukkit.getScheduler().runTaskAsynchronously(cowBot, () -> {
        //noinspection UnstableApiUsage
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("trevorrequest|" + cowBot.getCurrentServer().getServerName());
        out.writeUTF(event.getPlayer().getName());
        event.getPlayer().sendPluginMessage(cowBot, "trevor:main", out.toByteArray());
      });
    }

    if (event.getPlayer().hasPermission("trevor.admin") && event.getMessage().equalsIgnoreCase("hey trevor can you purge the normie cache")) {
      chatEnumCache.clear();
      cowBot.getServer().getScheduler().runTaskLaterAsynchronously(cowBot, () -> cowBot.getServer().broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.WHITE + "sure dad :)"), 10);
    }

    if (event.getPlayer().hasPermission("trevor.admin") && event.getMessage().equalsIgnoreCase("hey trevor can you reload the config")) {
      cowBot.reloadConfig();
      cowBot.getServer().getScheduler().runTaskLaterAsynchronously(cowBot, () -> cowBot.getServer().broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.WHITE + "yeye consider it done :)"), 10);
    }

    if (silence && !event.getPlayer().hasPermission("trevor.mod")) {
      event.getPlayer().sendMessage(org.bukkit.ChatColor.AQUA + "Chat>> " + org.bukkit.ChatColor.YELLOW + "That chat is silenced!");
      event.setCancelled(true);
      return;
    }

    ChatEnum chatEnum = chatEnumCache.getOrDefault(event.getPlayer().getName(), ChatEnum.UNKNOWN);
    boolean hasChatEnum = chatEnum != null && chatEnum != ChatEnum.UNKNOWN;
    String name = (hasChatEnum ? ChatColor.stripColor(event.getPlayer().getDisplayName()) : event.getPlayer().getDisplayName());

    String prefix = ChatColor.translateAlternateColorCodes('&', prefixes.getOrDefault(event.getPlayer().getUniqueId(), "")).trim();
    event.setFormat(prefix + (prefix.equalsIgnoreCase("") ? "" : " ") + ChatColor.GRAY + "<" + (hasChatEnum ? (chatEnum.getChatColor() == null ? ChatColor.RED + "[Non-Sub] " + ChatColor.GRAY : chatEnum.getChatColor()) : "") + name + ChatColor.GRAY + "> " + ChatColor.WHITE + "%2$s");//event.getMessage().replaceAll("(?:[^%]|^)(?:(%%)+|)(%)(?:[^%])\n", "%%").replaceAll("%", "%%"));
    if (hasChatEnum && chatEnum.getChatColor() == null) {
      prefix = ChatColor.RED + "[Non-Sub] " + prefix;
    }
    cowBot.getServer().getPluginManager().callEvent(new AsyncFinishedChatEvent(prefix, event.getPlayer().getDisplayName(), event.getMessage()));
  }

  @SuppressWarnings("UnstableApiUsage")
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event) {
    if (!event.getPlayer().hasPlayedBefore()) {
      Bukkit.broadcastMessage(ChatColor.YELLOW + event.getPlayer().getName() + " has joined for the first time!");
    }
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

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onConnect(AsyncPlayerPreLoginEvent event) {
    if (rollbackQueue.contains(event.getUniqueId())) {
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "A rollback is currently being processed, please try logging in again later");
    }
  }

  public void addRollbackQueue(UUID uuid) {
    rollbackQueue.add(uuid);
  }

  public void removeRollbackQueue(UUID uuid) {
    rollbackQueue.remove(uuid);
  }

  public boolean isSilence() {
    return silence;
  }

  public void setSilence(boolean silence) {
    this.silence = silence;
  }
}
