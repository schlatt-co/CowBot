package io.github.jroy.cowbot;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.commands.spigot.CommunismCommand;
import io.github.jroy.cowbot.commands.spigot.ServerCommand;
import io.github.jroy.cowbot.utils.ChatEnum;
import io.github.jroy.cowbot.utils.ConsoleInterceptor;
import io.github.jroy.cowbot.utils.FakeCommandSender;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class CowBot extends JavaPlugin implements Listener, PluginMessageListener {

  private boolean isVanilla = true;

  private Map<String, ChatEnum> chatEnumCache = new HashMap<>();

  private List<Player> sleeping = new ArrayList<>();
  public HashMap<UUID, Boolean> communists = new HashMap<>();

  private WebhookClient webhookClient;
  private WebhookClient consoleWebhookClient;

  @Override
  public void onLoad() {
    log("Hello <3 -Trevor");
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void onEnable() {
    log("Running onEnable flow...");
    isVanilla = Bukkit.getWorld("world") != null;
    getServer().getPluginManager().registerEvents(this, this);
    getCommand("fuckbungee").setExecutor(new ServerCommand(this));
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    getServer().getMessenger().registerOutgoingPluginChannel(this, "trevor:main");
    getServer().getMessenger().registerIncomingPluginChannel(this, "trevor:main", this);
    getServer().getMessenger().registerIncomingPluginChannel(this, "trevor:discord", this);
    if (isVanilla) {
      getCommand("communism").setExecutor(new CommunismCommand(this));
    }
    log("Connecting to webhooks...");
    loadConfig();
    webhookClient = new WebhookClientBuilder(getConfig().getString("webhookUrl")).setDaemon(true).build();
    webhookClient.send(":white_check_mark: Server has started");
    consoleWebhookClient = new WebhookClientBuilder(getConfig().getString("consoleUrl")).setDaemon(true).build();
    new ConsoleInterceptor(this);
  }

  @Override
  public void onDisable() {
    if (webhookClient != null) {
      webhookClient.send(":octagonal_sign: Server has stopped");
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
        getServer().dispatchCommand(new FakeCommandSender(this, getServer().getConsoleSender()), in.readUTF());
      }
    }
  }

  @SuppressWarnings("UnstableApiUsage")
  public void sendToServer(Player player, String server) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("ConnectOther");
    out.writeUTF(player.getName());
    out.writeUTF(server);
    player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
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
      event.setFormat(prefix + ChatColor.GRAY + "<" + chatEnum.getChatColor() + ChatColor.stripColor(event.getPlayer().getDisplayName()) + ChatColor.GRAY + "> " + ChatColor.WHITE + event.getMessage().replaceAll("(?:[^%]|\\\\A)%(?:[^%]|\\\\z)", "%%"));
      webhookClient.send(ChatColor.stripColor(prefix + event.getPlayer().getDisplayName() + " >> " + event.getMessage()));
      return;
    }
    event.setFormat(prefix + ChatColor.GRAY + "<" + event.getPlayer().getDisplayName() + ChatColor.GRAY + "> " + ChatColor.WHITE + event.getMessage().replaceAll("(?:[^%]|\\\\A)%(?:[^%]|\\\\z)", "%%"));
    webhookClient.send(ChatColor.stripColor(prefix + event.getPlayer().getDisplayName() + " >> " + event.getMessage()));
  }

  @SuppressWarnings("UnstableApiUsage")
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event) {
    if (isVanilla) {
      if (!event.getPlayer().hasPlayedBefore()) {
        communists.put(event.getPlayer().getUniqueId(), false);
        event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 16, 54, -3, -90, 0));
      }
    }
    if (!chatEnumCache.containsKey(event.getPlayer().getName()) || chatEnumCache.get(event.getPlayer().getName()).equals(ChatEnum.UNKNOWN)) {
      chatEnumCache.put(event.getPlayer().getName(), ChatEnum.UNKNOWN);
      Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("trevorrequest|" + (isVanilla ? "vanilla" : "farm"));
        out.writeUTF(event.getPlayer().getName());
        event.getPlayer().sendPluginMessage(this, "trevor:main", out.toByteArray());
      }, 30);
    }
    webhookClient.send(":heavy_plus_sign: **" + ChatColor.stripColor(event.getPlayer().getDisplayName()) + " has joined the server" + (event.getPlayer().hasPlayedBefore() ? "!" : " for the first time!") + "**");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLeave(PlayerQuitEvent event) {
    if (isVanilla) {
      if (communists.containsKey(event.getPlayer().getUniqueId()) && !communists.get(event.getPlayer().getUniqueId())) {
        //noinspection ConstantConditions
        new File(new File(Bukkit.getServer().getWorld("world").getWorldFolder(), "playerdata"), event.getPlayer().getUniqueId().toString() + ".dat").delete();
      }
    }
    webhookClient.send(":heavy_minus_sign: **" + ChatColor.stripColor(event.getPlayer().getDisplayName()) + " has left the server!**");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(PlayerDeathEvent event) {
    if (StringUtils.isBlank(event.getDeathMessage()) || !event.getEntityType().equals(EntityType.PLAYER)) {
      return;
    }
    webhookClient.send(":skull: **" + event.getDeathMessage() + "**");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onAchievement(PlayerAdvancementDoneEvent event) {
    event.getAdvancement();
    if (event.getAdvancement().getKey().getKey().contains("recipe/")) {
      return;
    }

    try {
      Object craftAdvancement = ((Object) event.getAdvancement()).getClass().getMethod("getHandle").invoke(event.getAdvancement());
      Object advancementDisplay = craftAdvancement.getClass().getMethod("c").invoke(craftAdvancement);
      if (!(boolean) advancementDisplay.getClass().getMethod("i").invoke(advancementDisplay)) {
        return;
      }
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
      return;
    }
    String rawAdvancementName = event.getAdvancement().getKey().getKey();
    String advancementName = Arrays.stream(rawAdvancementName.substring(rawAdvancementName.lastIndexOf("/") + 1).toLowerCase().split("_"))
        .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
        .collect(Collectors.joining(" "));
    webhookClient.send(":medal: **" + event.getPlayer().getDisplayName() + " has made the advancement " + advancementName + "**");
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event) {
    if (isVanilla) {
      if (communists.containsKey(event.getPlayer().getUniqueId()) && !communists.get(event.getPlayer().getUniqueId())) {
        event.setCancelled(true);
        event.getPlayer().sendMessage("You must agree to the rules before you can break blocks! Type /communism when you read and agree.");
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (isVanilla) {
      if (communists.containsKey(event.getPlayer().getUniqueId()) && !communists.get(event.getPlayer().getUniqueId())) {
        event.setCancelled(true);
        event.getPlayer().sendMessage("You must agree to the rules before you can place blocks! Type /communism when you read and agree.");
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onSleep(PlayerBedEnterEvent event) {
    if (isVanilla) {
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
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onUnSleep(PlayerBedLeaveEvent event) {
    if (isVanilla) {
      sleeping.remove(event.getPlayer());
    }
  }

  private void loadConfig() {
    getConfig().addDefault("webhookUrl", "url");
    getConfig().addDefault("consoleUrl", "url");
    getConfig().options().copyDefaults(true);
    saveConfig();
  }

  public WebhookClient getWebhookClient() {
    return webhookClient;
  }

  public WebhookClient getConsoleWebhookClient() {
    return consoleWebhookClient;
  }

  private void log(String message) {
    getLogger().info("[SPIGOT] " + message);
  }
}
