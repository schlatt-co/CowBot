package io.github.jroy.cowbot.utils;

import dev.tycho.stonks.api.perks.CompanyPerk;
import dev.tycho.stonks.model.core.Company;
import dev.tycho.stonks.model.core.Member;
import io.github.jroy.cowbot.managers.spigot.HomeManager;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public class CompanyHomePerk extends CompanyPerk {

  public CompanyHomePerk(Plugin plugin, HomeManager homeManager) {
    super(plugin, "Company Home", Material.COMPASS, 100000, new String[]{"This perk allows you and your company to",
        "set a home for your company and lets members to teleport to it!"}, new CompanySetHomePerkAction(homeManager), new CompanyTeleportHomePerkAction(homeManager));
  }

  @Override
  public void onPurchase(Company company, Member purchaser) {

  }
}
