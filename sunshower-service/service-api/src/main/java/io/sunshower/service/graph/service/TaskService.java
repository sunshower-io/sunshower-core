package io.sunshower.service.graph.service;

import io.sunshower.common.Identifier;
import io.sunshower.service.model.task.ExecutionMonitor;
import io.sunshower.service.model.task.ExecutionPlan;
import io.sunshower.service.task.TaskContext;

public interface TaskService {

    ExecutionMonitor plan(Identifier tid, GraphService service);
    
    ExecutionMonitor execute(Identifier tid, GraphService service);
    
    ExecutionMonitor execute(
            Identifier tid, 
            GraphService service, 
            TaskContext context
    );
    
}
