package io.github.jroy.cowbot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.managers.CommunismManager;
import io.github.jroy.cowbot.managers.SleepManager;
import io.github.jroy.cowbot.managers.WebhookManager;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.utils.AsyncFinishedChatEvent;
import io.github.jroy.cowbot.utils.ChatEnum;
import io.github.jroy.cowbot.utils.DispatchCommandSender;
import io.github.jroy.cowbot.utils.WebhookCommandSender;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CowBot extends JavaPlugin implements Listener, PluginMessageListener {

  private List<SpigotModule> loadedModules = new ArrayList<>();

  private WebhookManager webhookManager;

  private boolean isVanilla = true;
  private boolean isCreative = false;

  private Map<String, ChatEnum> chatEnumCache = new HashMap<>();

  @Override
  public void onLoad() {
    log("Hello <3 -Trevor");
  }

  @Override
  public void onEnable() {
    log("Loading CowBot...");
    isVanilla = Bukkit.getWorld("world") != null;
    isCreative = Bukkit.getWorld("creative") != null;
    getServer().getPluginManager().registerEvents(this, this);
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    getServer().getMessenger().registerOutgoingPluginChannel(this, "trevor:main");
    getServer().getMessenger().registerOutgoingPluginChannel(this, "trevor:discord");
    getServer().getMessenger().registerIncomingPluginChannel(this, "trevor:main", this);
    getServer().getMessenger().registerIncomingPluginChannel(this, "trevor:discord", this);
    if (isVanilla) {
      loadedModules.add(new CommunismManager(this));
      loadedModules.add(new SleepManager(this));
    }
    loadedModules.add(webhookManager = new WebhookManager(this));
  }

  @Override
  public void onDisable() {
    for (SpigotModule module : loadedModules) {
      module.onDisable();
    }
  }

  @SuppressWarnings("UnstableApiUsage")
  @Override
  public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
    if (channel.equalsIgnoreCase("trevor:main")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      if (in.readUTF().equalsIgnoreCase("trevorreturn")) {
        String[] input = in.readUTF().split(":");
        chatEnumCache.put(input[0], ChatEnum.valueOf(input[1]));
      }
    } else if (channel.equalsIgnoreCase("trevor:discord")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      String subchannel = in.readUTF();
      if (subchannel.equalsIgnoreCase("chat")) {
        String content = in.readUTF();
        getServer().broadcastMessage(ChatColor.AQUA + "[Trevorcord] " + ChatColor.YELLOW + content.split(":")[0] + ChatColor.AQUA + " >> " + ChatColor.WHITE + content.replaceFirst("(?:\\S(?: +)?)+:", ""));
      } else if (subchannel.equalsIgnoreCase("cmd")) {
        getServer().dispatchCommand(new WebhookCommandSender(webhookManager, getServer().getConsoleSender()), in.readUTF());
      } else if (subchannel.equalsIgnoreCase("dispatch")) {
        String msg = in.readUTF();
        getServer().dispatchCommand(new DispatchCommandSender(this, getServer().getConsoleSender(), msg.split(":")[0]), msg.replaceFirst(msg.split(":")[0] + ":", ""));
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChat(AsyncPlayerChatEvent event) {
    if (event.getPlayer().hasPermission("trevor.admin") && event.getMessage().equalsIgnoreCase("hey trevor can you purge the normie cache")) {
      chatEnumCache.clear();
      getServer().getScheduler().runTaskLaterAsynchronously(this, () -> getServer().broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.WHITE + "sure dad :)"), 10);
    }
    String prefix = "";
    if (event.getPlayer().hasPermission("trevor.admin")) {
      prefix = ChatColor.GRAY + "[" + ChatColor.RED + "Admin" + ChatColor.GRAY + "] ";
    } else if (event.getPlayer().hasPermission("trevor.mod")) {
      prefix = ChatColor.GRAY + "[" + ChatColor.GREEN + "Mod" + ChatColor.GRAY + "] ";
    } else if (event.getPlayer().hasPermission("trevor.twitch")) {
      prefix = ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Twitch" + ChatColor.GRAY + "] ";
    } else if (event.getPlayer().hasPermission("trevor.content")) {
      prefix = ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "Content" + ChatColor.GRAY + "] ";
    } else if (event.getPlayer().hasPermission("trevor.gay")) {
      prefix = ChatColor.RED + "[" + ChatColor.GOLD + "G" + ChatColor.YELLOW + "a" + ChatColor.GREEN + "y" + ChatColor.LIGHT_PURPLE + "] ";
    }

    ChatEnum chatEnum = chatEnumCache.getOrDefault(event.getPlayer().getName(), ChatEnum.UNKNOWN);
    boolean hasChatEnum = chatEnum != null && chatEnum != ChatEnum.UNKNOWN;
    event.setFormat(prefix + ChatColor.GRAY + "<" + (hasChatEnum ? chatEnum.getChatColor() : "") + (hasChatEnum ? ChatColor.stripColor(event.getPlayer().getDisplayName()) : event.getPlayer().getDisplayName()) + ChatColor.GRAY + "> " + ChatColor.WHITE + event.getMessage().replaceAll("(?:[^%]|^)(?:(%%)+|)(%)(?:[^%])\n", "%%"));
    getServer().getPluginManager().callEvent(new AsyncFinishedChatEvent(prefix, event.getPlayer().getDisplayName(), event.getMessage()));
  }

  @SuppressWarnings("UnstableApiUsage")
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event) {
    if (!chatEnumCache.containsKey(event.getPlayer().getName()) || chatEnumCache.get(event.getPlayer().getName()).equals(ChatEnum.UNKNOWN)) {
      chatEnumCache.put(event.getPlayer().getName(), ChatEnum.UNKNOWN);
      Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("trevorrequest|" + (isVanilla ? "vanilla" : (isCreative ? "creative" : "farm")));
        out.writeUTF(event.getPlayer().getName());
        event.getPlayer().sendPluginMessage(this, "trevor:main", out.toByteArray());
      }, 30);
    }
  }

  /**
   * This helps with a bug in minecraft which causes bats and fish not counting towards to mob cap.
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (event.getEntity() instanceof Bat || event.getEntity() instanceof Fish) {
      event.setCancelled(true);
    }
  }

  private void log(String message) {
    getLogger().info("[SPIGOT] " + message);
  }
}
