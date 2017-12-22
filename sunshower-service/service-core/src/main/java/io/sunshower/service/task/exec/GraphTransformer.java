package io.sunshower.service.task.exec;

import io.sunshower.service.hal.core.Graph;
import io.sunshower.service.task.TaskGraph;

@FunctionalInterface
public interface GraphTransformer {
    
    TaskGraph transform(Graph graph);
}
