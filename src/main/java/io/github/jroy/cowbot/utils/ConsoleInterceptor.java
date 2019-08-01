package io.github.jroy.cowbot.utils;

import io.github.jroy.cowbot.managers.spigot.WebhookManager;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import java.util.regex.Pattern;

public class ConsoleInterceptor extends AbstractAppender {

  private static final Pattern sanitizePattern = Pattern.compile("\\[m|\\[([0-9]{1,2}[;m]?){3}|\u001B+");

  private WebhookManager webhookManager;

  public ConsoleInterceptor(WebhookManager webhookManager) {
    super("Trevor-Console", null, null, false, null);
    this.webhookManager = webhookManager;
    ((Logger) LogManager.getRootLogger()).addAppender(this);
    start();
  }

  @Override
  public void append(LogEvent event) {
    String line = sanitize(event.getMessage().getFormattedMessage());
    if (StringUtils.isBlank(line)) {
      return;
    }
    webhookManager.sendConsoleWebhookMessage(line);
  }

  private String sanitize(String text) {
    if (StringUtils.isBlank(text)) {
      return null;
    }

    return sanitizePattern.matcher(text).replaceAll("");
  }
}
