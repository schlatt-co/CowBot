package io.github.jroy.cowbot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.commands.CommunismCommand;
import io.github.jroy.cowbot.commands.ServerShutdownCommand;
import io.github.jroy.cowbot.utils.ChatEnum;
import io.github.jroy.cowbot.utils.Logger;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class CowBot extends JavaPlugin implements Listener, PluginMessageListener {

  private Map<String, ChatEnum> chatEnumCache = new HashMap<>();

  private List<Player> sleeping = new ArrayList<>();
  public HashMap<UUID, Boolean> communists = new HashMap<>();
  public static CowBot instance;

  @Override
  public void onEnable() {
    Logger.log("Loading CowBot...");
    instance = this;
    getServer().getPluginManager().registerEvents(this, this);
    getCommand("communism").setExecutor(new CommunismCommand(this));
    getCommand("autorestart").setExecutor(new ServerShutdownCommand());
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    getServer().getMessenger().registerOutgoingPluginChannel(this, "trevor:main");
    getServer().getMessenger().registerIncomingPluginChannel(this, "trevor:main", this);
    ByteArrayDataOutput out1 = ByteStreams.newDataOutput();
    out1.writeUTF("target");
    out1.writeUTF("holding");
    getServer().sendPluginMessage(CowBot.instance, "trevor:main", out1.toByteArray());
  }

  @Override
  public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
    if (channel.equalsIgnoreCase("trevor:main")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      if (in.readUTF().equalsIgnoreCase("trevorreturn")) {
        String[] input = in.readUTF().split(":");
        chatEnumCache.put(input[0], ChatEnum.valueOf(input[1]));
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
      prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Content" + ChatColor.GRAY + "] ";
    } else if (event.getPlayer().hasPermission("trevor.gay")) {
      prefix = ChatColor.RED + "[" + ChatColor.GOLD + "G" + ChatColor.YELLOW + "a" + ChatColor.GREEN + "y" + ChatColor.LIGHT_PURPLE + "] ";
    }

    ChatEnum chatEnum = chatEnumCache.getOrDefault(event.getPlayer().getName(), ChatEnum.UNKNOWN);
    if (chatEnum != null && chatEnum != ChatEnum.UNKNOWN) {
      event.setFormat(prefix + ChatColor.GRAY + "<" + chatEnum.getChatColor() + ChatColor.stripColor(event.getPlayer().getDisplayName()) + ChatColor.GRAY + "> " + ChatColor.WHITE + event.getMessage());
      return;
    }
    event.setFormat(prefix + ChatColor.GRAY + "<" + event.getPlayer().getDisplayName() + ChatColor.GRAY + "> " + ChatColor.WHITE + event.getMessage());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event) {
    if (!event.getPlayer().hasPlayedBefore()) {
      communists.put(event.getPlayer().getUniqueId(), false);
      event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 16, 54, -3, -90, 0));
    }
    if (!chatEnumCache.containsKey(event.getPlayer().getName()) || chatEnumCache.get(event.getPlayer().getName()).equals(ChatEnum.UNKNOWN)) {
      chatEnumCache.put(event.getPlayer().getName(), ChatEnum.UNKNOWN);
      Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("trevorrequest");
        out.writeUTF(event.getPlayer().getName());
        event.getPlayer().sendPluginMessage(CowBot.instance, "trevor:main", out.toByteArray());
      }, 30);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onLeave(PlayerQuitEvent event) {
    if (communists.containsKey(event.getPlayer().getUniqueId()) && !communists.get(event.getPlayer().getUniqueId())) {
      new File(new File(Bukkit.getServer().getWorld("world").getWorldFolder(), "playerdata"), event.getPlayer().getUniqueId().toString() + ".dat").delete();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent event) {
    if (!event.getPlayer().isOp() && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Material.DRAGON_EGG.equals(Objects.requireNonNull(event.getClickedBlock()).getType())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
    if (event.getSourceBlock().getType().equals(Material.DRAGON_EGG)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event) {
    if (communists.containsKey(event.getPlayer().getUniqueId()) && !communists.get(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage("You must agree to the rules before you can break blocks! Type /communism when you read and agree.");
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (communists.containsKey(event.getPlayer().getUniqueId()) && !communists.get(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage("You must agree to the rules before you can place blocks! Type /communism when you read and agree.");
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSleep(PlayerBedEnterEvent event) {
    if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
      sleeping.add(event.getPlayer());
      event.getPlayer().setStatistic(Statistic.TIME_SINCE_REST, 0);
      Bukkit.broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.YELLOW + event.getPlayer().getName() + ChatColor.WHITE + " has started sleeping! " + ChatColor.YELLOW + ((event.getPlayer().getWorld().getPlayers().size() / 2) - sleeping.size()) + ChatColor.WHITE + " more player(s) need to sleep in order to advance to day!");
      if (sleeping.size() >= (event.getPlayer().getWorld().getPlayers().size() / 2)) {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[Trevor from Cowchop] " + ChatColor.WHITE + "Advancing to day!");
        //noinspection ConstantConditions
        Bukkit.getServer().getWorld(event.getPlayer().getWorld().getName()).setTime(1000L);
        event.getPlayer().getWorld().setStorm(false);
        event.getPlayer().getWorld().setThundering(false);
        sleeping.clear();
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onUnSleep(PlayerBedLeaveEvent event) {
    sleeping.remove(event.getPlayer());
  }
}
