package io.github.jroy.cowbot.utils;

import dev.tycho.stonks.api.perks.CompanyPerkAction;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Role;
import io.github.jroy.cowbot.managers.spigot.HomeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class CompanySetHomePerkAction extends CompanyPerkAction {

  private final HomeManager homeManager;

  public CompanySetHomePerkAction(HomeManager homeManager) {
    super("Set Home", Material.END_PORTAL_FRAME, Role.Manager, "Sets the company's home.");
    this.homeManager = homeManager;
  }

  @Override
  public void onExecute(Company company, Player executor) {
    try {
      if (homeManager.hasHome(company)) {
        homeManager.updateHome(company, executor.getLocation());
      } else {
        homeManager.addHome(company, executor.getLocation());
      }
      sendMessage(executor, "Successfully updated your companies' home!");
    } catch (SQLException e) {
      e.printStackTrace();
      sendMessage(executor, "An error occurred while setting home! Please report this to Joshie!");
    }
  }
}
