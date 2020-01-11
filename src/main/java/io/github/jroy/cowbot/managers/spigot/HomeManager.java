package io.github.jroy.cowbot.managers.spigot;

import dev.tycho.stonks.api.StonksAPI;
import dev.tycho.stonks.model.core.Company;
import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.utils.CompanyHomePerk;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;

public class HomeManager extends SpigotModule {

  private final CowBot cowBot;

  private Connection connection;

  public HomeManager(CowBot plugin) {
    super("Home Manager", plugin);
    this.cowBot = plugin;
  }

  @Override
  public void enable() {
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:" + cowBot.getDataFolder().getAbsolutePath() + "/homes.db");
      log("Connected to database!");
      connection.createStatement().execute("CREATE TABLE IF NOT EXISTS homes( id integer PRIMARY KEY AUTOINCREMENT, companyPk text NOT NULL, x text NOT NULL, y text NOT NULL, z text NOT NULL);");
      log("Database tables initialized!");
      StonksAPI.registerPerk(new CompanyHomePerk(plugin, this));
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
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

  public void addHome(Company company, Location location) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("INSERT INTO homes(companyPk, x, y, z) VALUES(?, ?, ?, ?)");
    statement.setString(1, String.valueOf(company.pk));
    statement.setString(2, String.valueOf(location.getBlockX()));
    statement.setString(3, String.valueOf(location.getBlockY()));
    statement.setString(4, String.valueOf(location.getBlockZ()));
    statement.executeUpdate();
  }

  public void updateHome(Company company, Location location) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("UPDATE homes SET x = ?, y = ?, z = ? WHERE companyPk = ?");
    statement.setString(1, String.valueOf(location.getBlockX()));
    statement.setString(2, String.valueOf(location.getBlockY()));
    statement.setString(3, String.valueOf(location.getBlockZ()));
    statement.setString(4, String.valueOf(company.pk));
    statement.executeUpdate();
  }

  public boolean hasHome(Company company) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT x, y, z FROM homes WHERE companyPk = ?");
    statement.setString(1, String.valueOf(company.pk));
    ResultSet rs = statement.executeQuery();
    return rs.next();
  }

  public Location getHome(Company company) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT x, y, z FROM homes WHERE companyPk = ?");
    statement.setString(1, String.valueOf(company.pk));
    ResultSet rs = statement.executeQuery();
    rs.next();
    return new Location(Bukkit.getWorld("world"), Double.parseDouble(rs.getString("x")), Double.parseDouble(rs.getString("y")), Double.parseDouble(rs.getString("z")));
  }
}
