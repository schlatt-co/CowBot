package io.github.jroy.cowbot.commands.discord;

import io.github.jroy.cowbot.ProxiedCow;
import io.github.jroy.cowbot.commands.discord.base.CommandBase;
import io.github.jroy.cowbot.commands.discord.base.CommandEvent;
import io.github.jroy.cowbot.managers.proxy.DiscordManager;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EvalCommand extends CommandBase {

  private ProxiedCow cow;
  private DiscordManager discordManager;

  public EvalCommand(ProxiedCow cow, DiscordManager discordManager) {
    super("eval", "<code>", "Evaluates Code");
    this.cow = cow;
    this.discordManager = discordManager;
  }

  @Override
  protected void executeCommand(CommandEvent e) {
    if (!e.isOwner()) {
      e.replyError("drown.");
      return;
    }

    if (e.getArgs().isEmpty()) {
      e.replyError(help);
      return;
    }

    ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
    se.put("e", e);
    se.put("jda", e.getJDA());
    se.put("guild", e.getGuild());
    se.put("channel", e.getChannel());
    se.put("textchannel", e.getTextChannel());
    se.put("member", e.getMember());
    se.put("cow", cow);
    se.put("db", discordManager);

    try {
//      se.eval("var Constants = Java.type(\"io.github.jroy.cowbot.utils.Constants\");");
//      se.eval("var ChatEnum = Java.type(\"io.github.jroy.cowbot.utils.ChatEnum\");");
//      se.eval("var ATLauncherUtils = Java.type(\"io.github.jroy.cowbot.utils.ATLauncherUtils\");");
      e.reply("Evaluated Successfully:\n```\n" + se.eval(e.getArgs()) + " ```");
    } catch (Exception ex) {
      e.reply("An exception was thrown:\n```\n" + ex + " ```");
    }
  }
}
