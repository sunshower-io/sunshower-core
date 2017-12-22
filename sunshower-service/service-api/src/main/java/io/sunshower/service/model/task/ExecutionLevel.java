package io.sunshower.service.model.task;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haswell on 2/8/17.
 */

@XmlRootElement(name = "level")
public class ExecutionLevel {

    @XmlAttribute
    private int level;

    @XmlElement(name = "task")
    @XmlElementWrapper(name = "tasks")
    private List<ExecutionTask> tasks;

    public ExecutionLevel() {
        this.level = 0;
        this.tasks = new ArrayList<>();

    }

    public ExecutionLevel(int level, List<ExecutionTask> tasks) {
        this.level = level;
        this.tasks = tasks;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<ExecutionTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<ExecutionTask> tasks) {
        this.tasks = tasks;
    }

    public void addTask(ExecutionTask executionTask) {
        this.tasks.add(executionTask);
    }

}
