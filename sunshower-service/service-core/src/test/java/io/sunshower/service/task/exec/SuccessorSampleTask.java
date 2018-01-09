package io.sunshower.service.task.exec;

import io.sunshower.service.task.Context;
import io.sunshower.service.task.Run;
import io.sunshower.service.task.Task;
import org.springframework.transaction.annotation.Transactional;

/** Created by haswell on 3/27/17. */
@Task(key = "io::sunshower::successor-task", definition = SuccessorSampleNode.class)
@Transactional
public class SuccessorSampleTask {

  @Context private String name;

  @Context SampleNode predecessor;

  @Run
  public SuccessorSampleNode run() throws InterruptedException {
    System.out.println("Running" + name);
    Thread.sleep((long) (1000 * Math.random()));
    System.out.println("Done");
    return new SuccessorSampleNode();
  }

  public String getName() {
    return name;
  }

  public SampleNode getPredecessor() {
    return predecessor;
  }
}
