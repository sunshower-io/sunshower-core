package io.sunshower.service.task.exec;

import io.sunshower.common.Identifier;

/** Created by haswell on 2/15/17. */
public class TaskException extends RuntimeException {
  final Exception cause;
  private final Identifier taskId;
  private final Identifier scheduleId;

  public TaskException(Exception e, Identifier scheduleId, Identifier taskId) {
    this.cause = e;
    this.taskId = taskId;
    this.scheduleId = scheduleId;
  }

  @Override
  public Exception getCause() {
    return cause;
  }

  public Identifier getTaskId() {
    return taskId;
  }

  public Identifier getScheduleId() {
    return scheduleId;
  }
}
