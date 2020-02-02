package io.github.jroy.cowbot.managers.proxy;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.proxy.TrevorCommand;
import io.github.jroy.cowbot.managers.base.ProxyModule;
import io.github.jroy.cowbot.utils.Constants;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("WeakerAccess")
public class DatabaseManager extends ProxyModule {

  private ProxiedCow proxiedCow;
  private DiscordManager discordManager;

  private PlayerConnectionManager playerConnectionManager;

  private Connection connection;

  public DatabaseManager(ProxiedCow proxiedCow, DiscordManager discordManager) {
    super("Database Manager", proxiedCow);
    this.proxiedCow = proxiedCow;
    this.discordManager = discordManager;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void enable() {
    try {
      if (!proxiedCow.getDataFolder().exists()) proxiedCow.getDataFolder().mkdir();

      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:" + proxiedCow.getDataFolder().getAbsolutePath() + "/players.db");
      log("Connected to database!");
      connection.createStatement().execute("CREATE TABLE IF NOT EXISTS players( id integer PRIMARY KEY AUTOINCREMENT, mc text NOT NULL, discordid text NOT NULL);");
      connection.createStatement().execute("CREATE TABLE IF NOT EXISTS bans( id integer PRIMARY KEY AUTOINCREMENT, discordid text NOT NULL, reason text NOT NULL);");
      log("Database tables initialized!");
      proxiedCow.getProxy().getScheduler().schedule(proxiedCow, () -> {
        try {
          proxiedCow.getProxy().getScheduler().runAsync(proxiedCow, () -> {
            log("Auditing server whitelist...");
            List<String> purgedUsers = new ArrayList<>();
            try {
              ResultSet whitelistSet = getUsers();
              while (whitelistSet.next()) {
                Member member = discordManager.getJda().getGuildById(Constants.GUILD_ID).getMemberById(whitelistSet.getString("discordid"));
                if (member == null || member.getRoles().isEmpty() || hasNoPaymentRole(member.getRoles())) {
                  if (proxiedCow.getProxy().getPlayer(whitelistSet.getString("mc")) != null) {
                    proxiedCow.getProxy().getPlayer(whitelistSet.getString("mc")).disconnect(new TextComponent("Hey you little shit!!!! your sub is up!!\nAHHAHA so fuck you\nbuy money"));
                  }
                  deleteUser(whitelistSet.getInt("id"));
                  purgedUsers.add(whitelistSet.getString("mc"));
                }
              }
              log("Purged " + purgedUsers.size() + " user(s) from the whitelist!");
              if (purgedUsers.size() > 0) {
                StringBuilder sb = new StringBuilder("What's poppin boys, it's time to chop some bovine from the sub server:\n\n");
                for (String curName : purgedUsers) {
                  sb.append("**").append(curName).append("**\n");
                }
                sb.append("\nKeep giving Schlatt your money or face the consequences...");
                discordManager.getJda().getGuildById(Constants.GUILD_ID).getTextChannelById(Constants.LOG_CHANNEL_ID).sendMessage(sb.toString()).queue();
              }
            } catch (SQLException e) {
              log("Error while executing whitelist purge: " + e.getMessage());
              e.printStackTrace();
            }
          });
        } catch (Exception e) {
          log("Error while executing async purge: " + e.getMessage());
          e.printStackTrace();
        }
      }, 0, 1, TimeUnit.HOURS);
      log("Database purger successfully registered!");
    } catch (ClassNotFoundException | SQLException e) {
      log("Error while initializing database connection: " + e.getMessage());
      e.printStackTrace();
      proxiedCow.getProxy().stop();
    }
  }

  public boolean hasNoPaymentRole(List<Role> roles) {
    for (Role role : roles) {
      if (!role.getName().startsWith("_")) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void disable() {
    try {
      connection.close();
    } catch (SQLException e) {
      log("Error while closing connection: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void addCommands() {
    addCommand(new TrevorCommand(this, discordManager, playerConnectionManager));
  }

  public void setPlayerConnectionManager(PlayerConnectionManager playerConnectionManager) {
    this.playerConnectionManager = playerConnectionManager;
  }

  public ResultSet getUsers() throws SQLException {
    return connection.createStatement().executeQuery("SELECT id, mc, discordid FROM players");
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isWhitelisted(String mcName) {
    try {
      PreparedStatement statement = connection.prepareStatement("SELECT id, mc, discordid FROM players WHERE mc = ? COLLATE NOCASE");
      statement.setString(1, mcName);
      ResultSet rs = statement.executeQuery();
      return rs.next();
    } catch (SQLException ignored) {
    }
    return false;
  }

  public boolean isLinked(String discordId) {
    try {
      PreparedStatement statement = connection.prepareStatement("SELECT id, mc, discordid FROM players WHERE discordid = ?");
      statement.setString(1, discordId);
      return statement.executeQuery().next();
    } catch (SQLException ignored) {
    }
    return false;
  }

  public void linkUser(String discordId, String mcName) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("INSERT INTO players(mc, discordid) VALUES(?, ?)");
    statement.setString(1, mcName);
    statement.setString(2, discordId);
    statement.executeUpdate();
  }

  public void updateUser(String discordId, String mcName) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("UPDATE players SET mc = ? WHERE discordid = ?");
    statement.setString(1, mcName);
    statement.setString(2, discordId);
    statement.executeUpdate();
  }

  public void deleteUser(int id) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("DELETE FROM players WHERE id = ?");
    statement.setInt(1, id);
    statement.executeUpdate();
  }

  public void deleteUser(String mcName) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("DELETE FROM players WHERE mc = ? COLLATE NOCASE");
    statement.setString(1, mcName);
    statement.executeUpdate();
  }

  public String getDiscordIdFromUsername(String mcName) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT id, mc, discordid FROM players WHERE mc = ? COLLATE NOCASE");
    statement.setString(1, mcName);
    ResultSet rs = statement.executeQuery();
    rs.next();
    return rs.getString("discordid");
  }

  public String getUsernameFromDiscordId(String discordId) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT id, mc, discordid FROM players WHERE discordid = ? COLLATE NOCASE");
    statement.setString(1, discordId);
    ResultSet rs = statement.executeQuery();
    rs.next();
    return rs.getString("mc");
  }

  public void banUser(String discordId, String reason) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("INSERT INTO bans(discordid, reason) VALUES(?, ?)");
    statement.setString(1, discordId);
    statement.setString(2, reason);
    statement.executeUpdate();
  }

  public void pardonUser(String discordId) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("DELETE FROM bans WHERE discordid = ?");
    statement.setString(1, discordId);
    statement.executeUpdate();
  }

  public boolean isBanned(String discordId) {
    try {
      PreparedStatement statement = connection.prepareStatement("SELECT id FROM bans WHERE discordid = ? COLLATE NOCASE");
      statement.setString(1, discordId);
      return statement.executeQuery().next();
    } catch (SQLException ignored) {
    }
    return false;
  }

  public String getBanReason(String discordId) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT reason FROM bans WHERE discordid = ? COLLATE NOCASE");
    statement.setString(1, discordId);
    ResultSet set = statement.executeQuery();
    set.next();
    return set.getString("reason");
  }
}
