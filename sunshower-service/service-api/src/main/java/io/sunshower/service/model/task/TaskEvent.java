package io.sunshower.service.model.task;


import io.sunshower.common.Identifier;

/**
 * Created by haswell on 2/15/17.
 */
public class TaskEvent {

    private Type type;
    private Identifier taskId;
    private Identifier scheduleId;

    public enum Type {
        TaskBeginning,
        TaskComplete,
        SequenceBeginning, SequenceComplete, TaskError
    }


    public TaskEvent() {

    }
    public TaskEvent(Type type, Identifier scheduleId, Identifier taskId) {
        this.type = type;
        this.taskId = taskId;
        this.scheduleId = scheduleId;
    }



    public Type getType() {
        return type;
    }

    public Identifier getTaskId() {
        return taskId;
    }

    public Identifier getScheduleId() {
        return scheduleId;
    }
}
