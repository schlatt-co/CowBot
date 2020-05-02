package io.github.jroy.cowbot.utils;

import io.github.jroy.cowbot.managers.spigot.WebhookManager;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WebhookCommandSender implements ConsoleCommandSender {

  private final WebhookManager webhookManager;
  private final ConsoleCommandSender sender;

  public WebhookCommandSender(WebhookManager webhookManager, ConsoleCommandSender consoleCommandSender) {
    this.webhookManager = webhookManager;
    this.sender = consoleCommandSender;
  }

  @NotNull
  @Override
  public PermissionAttachment addAttachment(@NotNull Plugin arg0) {
    return sender.addAttachment(arg0);
  }

  @Override
  public PermissionAttachment addAttachment(@NotNull Plugin arg0, int arg1) {
    return sender.addAttachment(arg0, arg1);
  }

  @NotNull
  @Override
  public PermissionAttachment addAttachment(@NotNull Plugin arg0, @NotNull String arg1, boolean arg2) {
    return sender.addAttachment(arg0, arg1, arg2);
  }

  @Override
  public PermissionAttachment addAttachment(@NotNull Plugin arg0, @NotNull String arg1, boolean arg2, int arg3) {
    return sender.addAttachment(arg0, arg1, arg2, arg3);
  }

  @NotNull
  @Override
  public Set<PermissionAttachmentInfo> getEffectivePermissions() {
    return sender.getEffectivePermissions();
  }

  @Override
  public boolean hasPermission(@NotNull String arg0) {
    return sender.hasPermission(arg0);
  }

  @Override
  public boolean hasPermission(@NotNull Permission arg0) {
    return sender.hasPermission(arg0);
  }

  @Override
  public boolean isPermissionSet(@NotNull String arg0) {
    return sender.isPermissionSet(arg0);
  }

  @Override
  public boolean isPermissionSet(@NotNull Permission arg0) {
    return sender.isPermissionSet(arg0);
  }

  @Override
  public void recalculatePermissions() {
    sender.recalculatePermissions();
  }

  @Override
  public void removeAttachment(@NotNull PermissionAttachment arg0) {
    sender.removeAttachment(arg0);
  }

  @Override
  public boolean isOp() {
    return sender.isOp();
  }

  @Override
  public void setOp(boolean arg0) {
    sender.setOp(arg0);
  }

  @NotNull
  @Override
  public String getName() {
    return sender.getName();
  }

  @Override
  public @NotNull Spigot spigot() {
    return sender.spigot();
  }

  @NotNull
  @Override
  public Server getServer() {
    return sender.getServer();
  }

  @Override
  public void sendMessage(@NotNull String message) {
    webhookManager.sendWebhookMessage(message);
  }

  @Override
  public void sendMessage(String[] messages) {
    for (String msg : messages)
      sendMessage(msg);
  }

  @Override
  public void abandonConversation(@NotNull Conversation arg0) {
    sender.abandonConversation(arg0);
  }

  @Override
  public void abandonConversation(@NotNull Conversation arg0, @NotNull ConversationAbandonedEvent arg1) {
    sender.abandonConversation(arg0, arg1);
  }

  @Override
  public void acceptConversationInput(@NotNull String arg0) {
    sender.acceptConversationInput(arg0);
  }

  @Override
  public boolean beginConversation(@NotNull Conversation arg0) {
    return sender.beginConversation(arg0);
  }

  @Override
  public boolean isConversing() {
    return sender.isConversing();
  }

  @Override
  public void sendRawMessage(@NotNull String arg0) {
    sender.sendRawMessage(arg0);
  }

}
