package io.github.jroy.cowbot.utils;

public enum ServerType {

  VANILLA("vanilla", "world"),
  FARM("farm", "plots"),
  CREATIVE("creative", "creative"),
  UNKNOWN(null, null);

  private String serverName;
  private String worldName;

  ServerType(String serverName, String worldName) {
    this.serverName = serverName;
    this.worldName = worldName;
  }

  public String getServerName() {
    return serverName;
  }

  public String getWorldName() {
    return worldName;
  }
}
