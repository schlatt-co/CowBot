package io.github.jroy.cowbot.utils;

import net.md_5.bungee.api.ChatColor;

public enum ChatEnum {

  UNKNOWN(ChatColor.WHITE, 100),
  NONE(null, 99),
  CONTENT(ChatColor.DARK_GREEN, 50),
  MOD(ChatColor.GREEN, 40),
  PATREON(ChatColor.GOLD, 30),
  BOOST(ChatColor.LIGHT_PURPLE, 25),
  MERCH(ChatColor.RED, 20),
  TWITCH(ChatColor.BLUE, 1);

  private final ChatColor chatColor;
  private final int power;

  ChatEnum(ChatColor color, int power) {
    chatColor = color;
    this.power = power;
  }

  public ChatColor getChatColor() {
    return chatColor;
  }

  public int getPower() {
    return power;
  }
}
