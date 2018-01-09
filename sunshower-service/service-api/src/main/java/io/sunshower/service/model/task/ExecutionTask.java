package io.sunshower.service.model.task;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 2/8/17. */
@XmlRootElement(name = "task")
public class ExecutionTask {
  @XmlAttribute private int order;

  @XmlAttribute private String name;

  @XmlElement private String taskId;

  public ExecutionTask() {}

  public ExecutionTask(int order, String taskId, String name) {
    this.order = order;
    this.taskId = taskId;
    this.name = name;
  }

  @Override
  public String toString() {
    return "ExecutionTask{"
        + "order="
        + order
        + ", name='"
        + name
        + '\''
        + ", taskId='"
        + taskId
        + '\''
        + '}';
  }
}
