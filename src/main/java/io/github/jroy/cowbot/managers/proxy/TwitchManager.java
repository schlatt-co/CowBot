package io.github.jroy.cowbot.managers.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.proxy.TwitchCommand;
import io.github.jroy.cowbot.managers.base.ProxyModule;
import io.github.jroy.cowbot.utils.Constants;
import io.github.jroy.cowbot.utils.InviteToken;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitchManager extends ProxyModule implements EventListener {

  private final static String CLIENT_ID = "0dtry9dnzr9yuz29ywdccowvifeqvh";
//  private final static String REDIRECT_URL = "http%3A%2F%2Flocalhost";
  private final static String REDIRECT_URL = "http%3A%2F%2Fmc.schlatt.co%2Findex";
  private final static Pattern idPattern = Pattern.compile(".+,\"_id\":\"(\\w+)\",.+");

  private final OkHttpClient client = new OkHttpClient.Builder().build();
  private final HashMap<String, InviteToken> inviteMap = new HashMap<>();

  private final ProxiedCow plugin;
  private final DatabaseManager databaseManager;
  private final DiscordManager discordManager;

  public TwitchManager(DatabaseManager databaseManager, DiscordManager discordManager, ProxiedCow plugin) {
    super("Twitch Manager", plugin);
    this.plugin = plugin;
    this.databaseManager = databaseManager;
    this.discordManager = discordManager;
    TwitchManager twitchManager = this;
    new Thread(() -> {
      try {
        Thread.sleep(10000);
        discordManager.getJda().addEventListener(twitchManager);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }

  @Override
  public void addCommands() {
    addCommand(new TwitchCommand(plugin, this));
  }

  public Invite createInvite(UUID uuid) {
    Invite invite = Objects.requireNonNull(Objects.requireNonNull(discordManager.getJda().getGuildById(Constants.GUILD_ID)).getTextChannelById("551619721410117642")).createInvite().setMaxUses(1).setTemporary(true).setMaxAge(5L, TimeUnit.MINUTES).complete();
    inviteMap.put(invite.getCode(), new InviteToken(System.currentTimeMillis(), uuid));
    return invite;
  }

  @Override
  public void onEvent(@Nonnull GenericEvent event) {
    if (event instanceof GuildMemberJoinEvent) {
      GuildMemberJoinEvent joinEvent = (GuildMemberJoinEvent) event;
      Guild guild = Objects.requireNonNull(discordManager.getJda().getGuildById(Constants.GUILD_ID));
      Objects.requireNonNull(guild.getTextChannelById("551619721410117642")).retrieveInvites().queue(list -> {
        //noinspection unchecked
        HashMap<String, InviteToken> copy = (HashMap<String, InviteToken>) inviteMap.clone();
        for (Invite invite : list) {
          copy.remove(invite.getCode());
        }

        for (Map.Entry<String, InviteToken> entry : copy.entrySet()) {
          inviteMap.remove(entry.getKey());
          if ((System.currentTimeMillis() - entry.getValue().getEpoch()) < 300000) {
            try {
              Boolean status = databaseManager.isAuthorizedDiscordId(joinEvent.getMember().getId(), entry.getValue().getUuid());
              if (status == null) {
                databaseManager.addFreeloader(joinEvent.getMember().getId(), entry.getValue().getUuid());
              } else if (!status) {
                guild.kick(joinEvent.getMember()).queue();
                return;
              }
              guild.addRoleToMember(joinEvent.getMember(), Objects.requireNonNull(guild.getRoleById("700104491596513382"))).queue();
            } catch (SQLException sqlException) {
              sqlException.printStackTrace();
            }
          }
        }
      });
    }
  }

  public void addTwitchUser(String userId, UUID uuid) throws SQLException {
    databaseManager.addTwitchId(userId, uuid);
  }

  public Boolean isAuthorized(String userId, UUID uuid) throws SQLException {
    return databaseManager.isTwitchIdAuthorized(userId, uuid);
  }

  public String getUserId(String auth) throws IOException {
    String response = Objects.requireNonNull(client.newCall(buildRequest("https://api.twitch.tv/kraken/user", auth)).execute().body()).string();
    Matcher matcher = idPattern.matcher(response);
    if (!matcher.matches()) {
      throw new IOException(response);
    }
    return matcher.group(1);
  }

  public String getUserEmotes(String userId, String auth) throws IOException {
    return Objects.requireNonNull(client.newCall(buildRequest("https://api.twitch.tv/kraken/users/" + userId + "/emotes", auth)).execute().body()).string();
  }

  private Request buildRequest(String url, String auth) {
    return new Request.Builder().url(url)
        .addHeader("Accept", "application/vnd.twitchtv.v5+json")
        .addHeader("Client-ID", CLIENT_ID)
        .addHeader("Authorization", "OAuth " + auth)
        .build();
  }

  public String getOAuthURL() {
    return "https://id.twitch.tv/oauth2/authorize?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URL + "&response_type=token&scope=user_subscriptions%20user_read";
  }
}
