package io.github.jroy.cowbot.managers.proxy;

import com.vdurmont.emoji.EmojiParser;
import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.discord.*;
import io.github.jroy.cowbot.commands.discord.base.CommandFactory;
import io.github.jroy.cowbot.managers.base.ProxyModule;
import io.github.jroy.cowbot.utils.Constants;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.md_5.bungee.config.Configuration;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.Objects;

public class DiscordManager extends ProxyModule implements EventListener {

  private ProxiedCow proxiedCow;
  private Configuration configuration;

  private DatabaseManager databaseManager;

  private JDA jda;

  public DiscordManager(ProxiedCow proxiedCow, Configuration configuration) {
    super("Discord Manager", proxiedCow);
    this.proxiedCow = proxiedCow;
    this.configuration = configuration;
  }

  @Override
  public void enable() {
    log("Logging into JDA...");
    try {
      jda = new JDABuilder(AccountType.BOT)
          .setToken(configuration.getString("discord-token"))
          .setStatus(OnlineStatus.DO_NOT_DISTURB)
          .addEventListeners(new CommandFactory("!", ".").addCommands(
              new LinkCommand(this),
              new BanCommand(this),
              new NameCommand(this),
              new EvalCommand(proxiedCow, this),
              new JoinDateCommand()
          ).build(), this)
          .build().awaitReady();
      log("Logged into JDA!");
    } catch (InterruptedException | LoginException e) {
      log("Error while logging into JDA: " + e.getMessage());
      e.printStackTrace();
      proxiedCow.getProxy().stop();
    }
  }

  @Override
  public void disable() {
    jda.shutdown();
  }

  public void setDatabaseManager(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public DatabaseManager getDatabaseManager() {
    return databaseManager;
  }

  @Override
  public void onEvent(@Nonnull GenericEvent event) {
    if (event instanceof GuildMessageReceivedEvent) {
      GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;
      if ((e.getChannel().getId().equalsIgnoreCase(Constants.VANILLA_CHAT_CHANNEL_ID) || e.getChannel().getId().equalsIgnoreCase(Constants.FARM_CHAT_CHANNEL_ID) || e.getChannel().getId().equalsIgnoreCase(Constants.CREATIVE_CHAT_CHANNEL_ID)) && !e.getAuthor().isBot() && !e.isWebhookMessage()) {
        String target = (e.getChannel().getId().equalsIgnoreCase(Constants.VANILLA_CHAT_CHANNEL_ID) ? "vanilla" : (e.getChannel().getId().equalsIgnoreCase(Constants.CREATIVE_CHAT_CHANNEL_ID) ? "creative" : "farm"));
        if (e.getMessage().getContentRaw().startsWith("!c ") && Objects.requireNonNull(e.getMember()).getRoles().stream().anyMatch(role -> role.getId().equalsIgnoreCase("549775492580900878"))) {
          PluginMessageManager.sendMessage(proxiedCow, "trevor:discord", "cmd", e.getMessage().getContentRaw().replaceFirst("!c ", ""), target);
          return;
        }
        if (e.getMessage().getContentRaw().startsWith("!list")) {
          PluginMessageManager.sendMessage(proxiedCow, "trevor:discord", "cmd", "list", target);
          return;
        }
        String message = EmojiParser.parseToAliases(e.getMessage().getContentStripped());
        message = message + (e.getMessage().getAttachments().isEmpty() ? "" : (" " + e.getMessage().getAttachments().get(0).getUrl()));
        PluginMessageManager.sendMessage(proxiedCow, "trevor:discord", "chat", (e.getMember() == null ? e.getAuthor().getName() : e.getMember().getEffectiveName()) + ":" + message, target);
      }
    }
  }

  public JDA getJda() {
    return jda;
  }
}
