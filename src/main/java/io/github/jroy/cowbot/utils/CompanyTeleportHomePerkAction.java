package io.github.jroy.cowbot.utils;

import com.earth2me.essentials.Essentials;
import dev.tycho.stonks.api.perks.CompanyPerkAction;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Role;
import io.github.jroy.cowbot.managers.spigot.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CompanyTeleportHomePerkAction extends CompanyPerkAction {

  private final HomeManager homeManager;
  private final Essentials essentials;

  public CompanyTeleportHomePerkAction(HomeManager homeManager) {
    super("Teleport Home", Material.ENDER_PEARL, Role.Employee, "Teleports to your companies' home.");
    this.homeManager = homeManager;
    this.essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

  }

  @Override
  public void onExecute(Company company, Player executor) {
    try {
      if (!homeManager.hasHome(company)) {
        sendMessage(executor, "This company is yet to setup a company home. Please ask a manager to set the company's home.");
        return;
      }
      essentials.getUser(executor).getTeleport().now(homeManager.getHome(company), false, PlayerTeleportEvent.TeleportCause.COMMAND);
      sendMessage(executor, "Teleported!");
    } catch (Exception e) {
      e.printStackTrace();
      sendMessage(executor, "There was an issue while teleporting you to your companies' home. Please report this to Joshie!");
    }
  }
}
