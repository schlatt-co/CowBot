package io.github.jroy.cowbot.managers.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.proxy.DispatchCommand;
import io.github.jroy.cowbot.commands.proxy.GlobalMessageCommand;
import io.github.jroy.cowbot.commands.proxy.RestartCommand;
import io.github.jroy.cowbot.commands.proxy.ShoutCommand;
import io.github.jroy.cowbot.managers.base.ProxyModule;
import io.github.jroy.cowbot.utils.ChatEnum;
import io.github.jroy.cowbot.utils.Constants;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class PluginMessageManager extends ProxyModule {

  private ProxiedCow proxiedCow;
  private DiscordManager discordManager;
  private DatabaseManager databaseManager;
  private PlayerConnectionManager playerConnectionManager;

  public PluginMessageManager(ProxiedCow proxiedCow, DiscordManager discordManager, DatabaseManager databaseManager, PlayerConnectionManager playerConnectionManager) {
    super("Plugin Message Manager", proxiedCow);
    this.proxiedCow = proxiedCow;
    this.discordManager = discordManager;
    this.databaseManager = databaseManager;
    this.playerConnectionManager = playerConnectionManager;
  }

  @Override
  public void enable() {
    proxiedCow.getProxy().registerChannel("trevor:main");
    proxiedCow.getProxy().registerChannel("trevor:discord");
  }

  @Override
  public void addCommands() {
    addCommand(new RestartCommand(proxiedCow));
    addCommand(new DispatchCommand(proxiedCow));
    addCommand(new ShoutCommand());
    addCommand(new GlobalMessageCommand());
  }

  @SuppressWarnings("UnstableApiUsage")
  @EventHandler
  public void onPluginMessage(PluginMessageEvent event) {
    if (event.getTag().equalsIgnoreCase("trevor:main")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
      try {
        String[] subchannel = in.readUTF().split("\\|");
        if (subchannel[0].equalsIgnoreCase("trevorrequest")) {
          String returnServer = subchannel[1];
          String username = in.readUTF();
          String discordId = databaseManager.getDiscordIdFromUsername(username);
          @SuppressWarnings("ConstantConditions") Member member = discordManager.getJda().getGuildById(Constants.GUILD_ID).getMemberById(discordId);
          if (member != null) {
            ChatEnum topRole = null;
            for (Role role : member.getRoles()) {
              switch (role.getId()) {
                case "581340753708449793":
                case "474574079362596864": {
                  topRole = checkAndSet(topRole, ChatEnum.UNKNOWN);
                  break;
                }
                case "574958723911385098":
                case "556676267357765652": {
                  topRole = checkAndSet(topRole, ChatEnum.MOD);
                  break;
                }
                case "581910051766272036": {
                  topRole = checkAndSet(topRole, ChatEnum.CONTENT);
                  break;
                }
                case "609837405612277790":
                case "509400860132900884":
                case "509400824640700436":
                case "509400795750465596": {
                  topRole = checkAndSet(topRole, ChatEnum.PATREON);
                  break;
                }
                case "585535513230835742": {
                  topRole = checkAndSet(topRole, ChatEnum.BOOST);
                  break;
                }
                case "582653648891281409":
                case "525473755712192552":
                case "479782883633135647": {
                  topRole = checkAndSet(topRole, ChatEnum.MERCH);
                  break;
                }
                case "469920113655808000":
                case "461225008887365632": {
                  topRole = checkAndSet(topRole, ChatEnum.TWITCH);
                  break;
                }
                default: {
                  break;
                }
              }
            }
            if (topRole == null) {
              topRole = ChatEnum.UNKNOWN;
            }
            sendMessage(proxiedCow, "trevor:main", "trevorreturn", username + ":" + topRole.name(), returnServer);
          }
        } else if (subchannel[0].equalsIgnoreCase("target")) {
          playerConnectionManager.setTargetServer(in.readUTF());
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else if (event.getTag().equalsIgnoreCase("trevor:discord")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
      String subchannel = in.readUTF();
      if (subchannel.equalsIgnoreCase("dispatchreply")) {
        String msg = in.readUTF();
        proxiedCow.getProxy().getPlayer(msg.split(":")[0]).sendMessage(new TextComponent(msg.replaceFirst(msg.split(":")[0], "")));
      }
    }
  }

  private ChatEnum checkAndSet(ChatEnum current, ChatEnum change) {
    if (current != null && current.getPower() > change.getPower()) {
      return current;
    }
    return change;
  }

  public static void sendMessage(ProxiedCow proxiedCow, String channel, String subchannel, String message, String server) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(stream);
    try {
      out.writeUTF(subchannel);
      out.writeUTF(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
    proxiedCow.getProxy().getServerInfo(server).sendData(channel, stream.toByteArray());
  }
}
