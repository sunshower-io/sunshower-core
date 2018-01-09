package io.sunshower.service.task.exec;

import io.sunshower.service.task.Context;
import io.sunshower.service.task.Run;
import io.sunshower.service.task.Task;

/** Created by haswell on 3/27/17. */
@Task(key = "io::sunshower::tertiary-task", definition = TertiaryNode.class)
public class TertiaryDependent {

  @Context private TertiaryNode node;

  @Context private String name;

  @Context private SampleNode ancestor;

  @Context private SuccessorSampleNode predecessor;

  @Run
  public void run() {
    System.out.println("Running" + name);
  }

  public TertiaryNode getNode() {
    return node;
  }

  public SampleNode getAncestor() {
    return ancestor;
  }

  public SuccessorSampleNode getPredecessor() {
    return predecessor;
  }
}
