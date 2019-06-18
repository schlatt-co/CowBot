package io.github.jroy.cowbot.utils;

import net.md_5.bungee.api.ChatColor;

public enum ChatEnum {

  UNKNOWN(ChatColor.WHITE, 100),
  CONTENT(ChatColor.YELLOW,50),
  MOD(ChatColor.GREEN, 40),
  PATREON(ChatColor.GOLD, 30),
  MERCH(ChatColor.RED, 20),
  TWITCH(ChatColor.BLUE, 1);

  private ChatColor chatColor;
  private int power;

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
