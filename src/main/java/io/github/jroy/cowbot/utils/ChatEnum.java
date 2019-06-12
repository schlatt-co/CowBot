package io.github.jroy.cowbot.utils;

import net.md_5.bungee.api.ChatColor;

public enum ChatEnum {

  UNKNOWN(ChatColor.WHITE),
  CONTENT(ChatColor.YELLOW),
  MOD(ChatColor.GREEN),
  PATREON(ChatColor.GOLD),
  TWITCH(ChatColor.BLUE);

  private ChatColor chatColor;

  ChatEnum(ChatColor color) {
    chatColor = color;
  }

  public ChatColor getChatColor() {
    return chatColor;
  }
}
