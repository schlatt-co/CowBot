package io.github.jroy.cowbot.managers.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.CowBot;
import io.github.jroy.cowbot.managers.base.SpigotModule;
import io.github.jroy.cowbot.utils.ChatEnum;
import io.github.jroy.cowbot.utils.DispatchCommandSender;
import io.github.jroy.cowbot.utils.WebhookCommandSender;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class PluginMessageManager extends SpigotModule implements PluginMessageListener {

  private CowBot cowBot;
  private WebhookManager webhookManager;
  private ChatManager chatManager;

  public PluginMessageManager(CowBot cowBot, WebhookManager webhookManager, ChatManager chatManager) {
    super("Plugin Message Manager", cowBot);
    this.cowBot = cowBot;
    this.webhookManager = webhookManager;
    this.chatManager = chatManager;
  }

  @Override
  public void enable() {
    cowBot.getServer().getMessenger().registerOutgoingPluginChannel(cowBot, "trevor:main");
    cowBot.getServer().getMessenger().registerOutgoingPluginChannel(cowBot, "trevor:discord");
    cowBot.getServer().getMessenger().registerIncomingPluginChannel(cowBot, "trevor:main", this);
    cowBot.getServer().getMessenger().registerIncomingPluginChannel(cowBot, "trevor:discord", this);
  }

  @SuppressWarnings("UnstableApiUsage")
  @Override
  public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
    if (channel.equalsIgnoreCase("trevor:main")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      if (in.readUTF().equalsIgnoreCase("trevorreturn")) {
        String[] input = in.readUTF().split(":");
        chatManager.chatEnumCache.put(input[0], ChatEnum.valueOf(input[1]));
      }
    } else if (channel.equalsIgnoreCase("trevor:discord")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      String subchannel = in.readUTF();
      if (subchannel.equalsIgnoreCase("chat")) {
        String content = in.readUTF();
        cowBot.getServer().broadcastMessage(ChatColor.AQUA + "[Trevorcord] " + ChatColor.YELLOW + content.split(":")[0] + ChatColor.AQUA + " >> " + ChatColor.WHITE + content.replaceFirst("(?:\\S(?: +)?)+:", ""));
      } else if (subchannel.equalsIgnoreCase("cmd")) {
        cowBot.getServer().dispatchCommand(new WebhookCommandSender(webhookManager, cowBot.getServer().getConsoleSender()), in.readUTF());
      } else if (subchannel.equalsIgnoreCase("dispatch")) {
        String msg = in.readUTF();
        cowBot.getServer().dispatchCommand(new DispatchCommandSender(cowBot, cowBot.getServer().getConsoleSender(), msg.split(":")[0]), msg.replaceFirst(msg.split(":")[0] + ":", ""));
      }
    }
  }
}
