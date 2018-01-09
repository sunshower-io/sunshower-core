package io.sunshower.service.model.task;

import io.sunshower.service.task.ElementDescriptor;
import io.sunshower.service.task.Status;

/** Created by haswell on 3/27/17. */
public class TaskDefinition {
  private Status status;
  private ElementDescriptor<?> descriptor;

  public TaskDefinition(ElementDescriptor resolved) {
    this.descriptor = resolved;
  }

  public String getName() {
    return descriptor.getName();
  }

  public Status getStatus() {
    return status;
  }

  @SuppressWarnings("unchecked")
  public <T> T getInstance() {
    return (T) descriptor.getInstance();
  }
}
