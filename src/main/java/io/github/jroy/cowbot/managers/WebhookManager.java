package io.github.jroy.cowbot.managers;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.utils.AsyncFinishedChatEvent;
import io.github.jroy.cowbot.utils.ConsoleInterceptor;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WebhookManager extends SpigotModule {

  private CowBot cowBot;

  private WebhookClient webhookClient;
  private WebhookClient consoleWebhookClient;

  public WebhookManager(CowBot cowBot) {
    super("Webhook Manager", cowBot);
    this.cowBot = cowBot;
  }

  @Override
  public void enable() {
    log("Loading config...");
    cowBot.getConfig().addDefault("webhookUrl", "url");
    cowBot.getConfig().addDefault("consoleUrl", "url");
    cowBot.getConfig().options().copyDefaults(true);
    cowBot.saveConfig();
    log("Config loaded!");
    log("Connecting to webhooks...");
    //noinspection ConstantConditions
    webhookClient = new WebhookClientBuilder(cowBot.getConfig().getString("webhookUrl")).setDaemon(true).build();
    sendWebhookMessage(":white_check_mark: Server has started");
    //noinspection ConstantConditions
    consoleWebhookClient = new WebhookClientBuilder(cowBot.getConfig().getString("consoleUrl")).setDaemon(true).build();
    new ConsoleInterceptor(this);
  }

  @Override
  public void disable() {
    if (webhookClient != null) {
      sendWebhookMessage(":octagonal_sign: Server has stopped");
      webhookClient.close();
    }
    if (consoleWebhookClient != null) {
      consoleWebhookClient.close();
    }
  }

  public void sendWebhookMessage(String message) {
    webhookClient.send(sanitizeWebhookMessage(message));
  }

  public void sendConsoleWebhookMessage(String message) {
    consoleWebhookClient.send(sanitizeWebhookMessage(message));
  }

  private String sanitizeWebhookMessage(String message) {
    return ChatColor.stripColor(message).replaceAll("@everyone", "@ everyone").replaceAll("\\[m|\\[([0-9]{1,2}[;m]?){3}|\u001B+", "");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(PlayerJoinEvent event) {
    sendWebhookMessage(":heavy_plus_sign: **" + event.getPlayer().getDisplayName() + " has joined the server" + (event.getPlayer().hasPlayedBefore() ? "!" : " for the first time!") + "**");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLeave(PlayerQuitEvent event) {
    sendWebhookMessage(":heavy_minus_sign: **" + event.getPlayer().getDisplayName() + " has left the server!**");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(PlayerDeathEvent event) {
    if (StringUtils.isBlank(event.getDeathMessage()) || !event.getEntityType().equals(EntityType.PLAYER)) {
      return;
    }
    sendWebhookMessage(":skull: **" + event.getDeathMessage() + "**");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onChat(AsyncFinishedChatEvent event) {
    sendWebhookMessage(event.getPrefix() + event.getDisplayName() + " >> " + event.getMessage());
  }

//  @EventHandler(priority = EventPriority.MONITOR)
//  public void onAchievement(PlayerAdvancementDoneEvent event) {
//    event.getAdvancement();
//    if (event.getAdvancement().getKey().getKey().contains("recipe/")) {
//      return;
//    }
//
//    try {
//      Object craftAdvancement = ((Object) event.getAdvancement()).getClass().getMethod("getHandle").invoke(event.getAdvancement());
//      Object advancementDisplay = craftAdvancement.getClass().getMethod("c").invoke(craftAdvancement);
//      if (!(boolean) advancementDisplay.getClass().getMethod("i").invoke(advancementDisplay)) {
//        return;
//      }
//    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NullPointerException e) {
//      return;
//    }
//    String rawAdvancementName = event.getAdvancement().getKey().getKey();
//    String advancementName = Arrays.stream(rawAdvancementName.substring(rawAdvancementName.lastIndexOf("/") + 1).toLowerCase().split("_"))
//        .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
//        .collect(Collectors.joining(" "));
//    sendWebhookMessage(":medal: **" + event.getPlayer().getDisplayName() + " has made the advancement " + advancementName + "**");
//  }
}
