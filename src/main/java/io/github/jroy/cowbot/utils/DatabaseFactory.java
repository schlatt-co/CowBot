package io.github.jroy.cowbot.utils;

import io.github.jroy.cowbot.ProxiedCow;
import net.dv8tion.jda.api.entities.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DatabaseFactory {

  private ProxiedCow cow;

  private Connection connection;

  public DatabaseFactory(ProxiedCow cow) throws SQLException, ClassNotFoundException {
    this.cow = cow;
    cow.log("Connecting to database...");
    connect();
  }

  @SuppressWarnings("ConstantConditions")
  private void connect() throws ClassNotFoundException, SQLException {
    if (!cow.getDataFolder().exists()) cow.getDataFolder().mkdir();

    Class.forName("org.sqlite.JDBC");
    connection = DriverManager.getConnection("jdbc:sqlite:" + cow.getDataFolder().getAbsolutePath() + "/players.db");
    cow.log("Connected to database!");
    connection.createStatement().execute("CREATE TABLE IF NOT EXISTS players( id integer PRIMARY KEY AUTOINCREMENT, mc text NOT NULL, discordid text NOT NULL);");
    connection.createStatement().execute("CREATE TABLE IF NOT EXISTS bans( id integer PRIMARY KEY AUTOINCREMENT, discordid text NOT NULL, reason text NOT NULL);");
    connection.createStatement().execute("CREATE TABLE IF NOT EXISTS vives( id integer PRIMARY KEY AUTOINCREMENT, mc text NOT NULL);");
    cow.log("Database tables initialized!");
    cow.getProxy().getScheduler().schedule(cow, () -> {
      cow.log("Auditing server whitelist...");
      List<String> purgedUsers = new ArrayList<>();
      try {
        ResultSet whitelistSet = getUsers();
        while (whitelistSet.next()) {
          Member member = cow.getJda().getGuildById(Constants.GUILD_ID).getMemberById(whitelistSet.getString("discordid"));
          if (member == null || member.getRoles().isEmpty()) {
            purgedUsers.add(whitelistSet.getString("mc"));
            deleteUser(whitelistSet.getInt("id"));
          }
        }
        cow.log("Purged " + purgedUsers.size() + " user(s) from the whitelist!");
        if (purgedUsers.size() > 0) {
          StringBuilder sb = new StringBuilder("What's poppin boys, it's time to chop some bovine from the sub server:\n\n");
          for (String curName : purgedUsers) {
            sb.append("**").append(curName).append("**\n");
            new Thread(() -> ATLauncherUtils.removePlayer(curName)).start();
          }
          sb.append("\nKeep giving Schlatt your money or face the consequences...");
          cow.getJda().getGuildById(Constants.GUILD_ID).getTextChannelById(Constants.LOG_CHANNEL_ID).sendMessage(sb.toString()).queue();
        }
      } catch (SQLException e) {
        cow.getLogger().log(Level.SEVERE, "[Proxy] Error while executing whitelist purge", e);
      }
    }, 0, 1, TimeUnit.HOURS);
    cow.log("Database purger successfully registered!");
  }

  private ResultSet getUsers() throws SQLException {
    return connection.createStatement().executeQuery("SELECT id, mc, discordid FROM players");
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isWhitelisted(String mcName) {
    try {
      PreparedStatement statement = connection.prepareStatement("SELECT id, mc, discordid FROM players WHERE mc = ? COLLATE NOCASE");
      statement.setString(1, mcName);
      ResultSet rs = statement.executeQuery();
      return rs.next();
    } catch (SQLException ignored) {}
    return false;
  }

  public boolean isLinked(String discordId) {
    try {
      PreparedStatement statement = connection.prepareStatement("SELECT id, mc, discordid FROM players WHERE discordid = ?");
      statement.setString(1, discordId);
      return statement.executeQuery().next();
    } catch (SQLException ignored) {}
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

  private void deleteUser(int id) throws SQLException {
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
    } catch (SQLException ignored) {}
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
