package io.github.jroy.cowbot.managers.proxy.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class StarMessages extends ListenerAdapter {

  private final Set<String> alreadyUsedMessages = new HashSet<>();

  @Override
  public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
    if (e.getReactionEmote().getName().equals("⭐")) {
      handleStar(e);
    }
  }

  private void handleStar(GuildMessageReactionAddEvent e) {
    e.getChannel().retrieveMessageById(e.getMessageId()).queue(message -> CompletableFuture.runAsync(new HandleStar(e, message)));
  }

  private void sendStarredMessage(String footer, Message message) {
    EmbedBuilder embed = new EmbedBuilder()
        .setTitle(message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator())
        .setDescription(message.getContentRaw());

    if (message.getEmbeds().size() > 0) {
      message.getChannel().sendMessage("Failed to Star/Gild a Message: Contained an un-readable embed! Cannot Continue!").queue();
      return;
    }

    embed.setFooter(footer, null);
    embed.setTimestamp(Instant.now());

    embed.setThumbnail(Objects.requireNonNull(message.getMember()).getUser().getAvatarUrl())
        .setColor(message.getMember().getColor());
    if (containsImage(message)) {
      embed.setImage(getImage(message));
    }

    Objects.requireNonNull(message.getGuild().getTextChannelById("672997238502457344")).sendMessage(embed.build()).queue();
    alreadyUsedMessages.add(message.getId());
  }

  /**
   * Tests for an image in target message.
   *
   * @param message The target message to be tested.
   * @return If the message contains an image.
   */
  public boolean containsImage(Message message) {
    if (message.getAttachments().stream().anyMatch(Message.Attachment::isImage)) {
      return true;
    }
    return message.getEmbeds().stream().anyMatch(e -> e.getImage() != null || e.getVideoInfo() != null);
  }

  /**
   * Gets the image url from a message.
   *
   * @param message The target message.
   * @return The image url.
   */
  public String getImage(Message message) {
    return message.getAttachments().get(0).getUrl();
  }

  private class HandleStar implements Runnable {

    private static final int NUM_STARS_REQUIRED = 12;
    private final GuildMessageReactionAddEvent e;
    private final Message message;

    HandleStar(GuildMessageReactionAddEvent e, Message message) {
      this.e = e;
      this.message = message;
    }

    @Override
    public void run() {
      if (e.getChannel().getId().equals("672997238502457344") || alreadyUsedMessages.contains(message.getId())) {
        return;
      }

      try {
        // total number of stars and shoes combined
        int numberOfStars = message.getReactions().stream()
            .filter(reaction -> reaction.getReactionEmote().getName().equals("⭐"))
            .mapToInt(MessageReaction::getCount)
            .sum();

        if (numberOfStars >= NUM_STARS_REQUIRED && !alreadyUsedMessages.contains(message.getId())) {
          String footer = "New starred message from #" + message.getChannel().getName();
          sendStarredMessage(footer, message);
          alreadyUsedMessages.add(message.getId());
        }
      } catch (NullPointerException | IllegalStateException ignored) {
      }
    }
  }
}
