package io.sunshower.service.task.exec;

import io.sunshower.service.model.task.TaskLogger;
import io.sunshower.service.task.Context;
import io.sunshower.service.task.Run;
import io.sunshower.service.task.Task;

import javax.inject.Inject;

/**
 * Created by haswell on 3/26/17.
 */
@Task(
        key = "io::sunshower::sample-task",
        definition = SampleNode.class
)
public class SampleTask {

    @Context
    int value;

    @Context
    private TaskLogger taskLogger;

    @Context
    private String name;

    @Context
    private SampleNode sampleNode;

    @Run
    public SampleNode run() throws InterruptedException {
        taskLogger.infof("RUNNING %s", sampleNode.getName());
        Thread.sleep((long) (60 * Math.random()));
        taskLogger.infof("Finished %s", sampleNode.getName());
        return sampleNode;
    }

    public SampleNode getNode() {
        return sampleNode;
    }

    public String getName() {
        return name;
    }
}
