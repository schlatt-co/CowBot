package io.github.jroy.cowbot.utils;

import net.md_5.bungee.api.ChatColor;

public enum ChatEnum {

  UNKNOWN(ChatColor.WHITE, 100),
  CONTENT(ChatColor.YELLOW,5),
  MOD(ChatColor.GREEN, 4),
  PATREON(ChatColor.GOLD, 3),
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
