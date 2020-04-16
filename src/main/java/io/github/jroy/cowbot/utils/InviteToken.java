package io.github.jroy.cowbot.utils;

import java.util.UUID;

public class InviteToken {

  private final Long epoch;
  private final UUID uuid;

  public InviteToken(Long epoch, UUID uuid) {
    this.epoch = epoch;
    this.uuid = uuid;
  }

  public Long getEpoch() {
    return epoch;
  }

  public UUID getUuid() {
    return uuid;
  }
}
