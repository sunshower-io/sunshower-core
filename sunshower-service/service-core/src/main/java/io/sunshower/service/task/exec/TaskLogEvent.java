package io.sunshower.service.task.exec;

import io.sunshower.service.model.task.TaskEvent;
import java.util.logging.Level;

public class TaskLogEvent extends TaskEvent {
  final Level level;
  final String template;
  final Object[] arguments;

  public TaskLogEvent(Level level, String format, Object... args) {
    this.level = level;
    this.template = format;
    this.arguments = args;
  }

  public String toString() {
    return String.format("%s: %s", level, String.format(template, arguments));
  }

  public Level getLevel() {
    return level;
  }

  public String getTemplate() {
    return template;
  }

  public Object[] getArguments() {
    return arguments;
  }
}
