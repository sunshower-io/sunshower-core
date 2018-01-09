package io.sunshower.service.task.exec;

import io.sunshower.common.Identifier;
import io.sunshower.service.graph.service.GraphService;
import io.sunshower.service.graph.service.TaskService;
import io.sunshower.service.hal.core.Graph;
import io.sunshower.service.model.task.ExecutionMonitor;
import io.sunshower.service.task.TaskContext;
import io.sunshower.service.task.TaskGraph;
import io.sunshower.service.task.Tasks;
import javax.inject.Inject;

public class DefaultTaskService implements TaskService {

  @Inject private GraphTransformer transformer;

  @Inject private ParallelTaskExecutor executor;

  @Override
  public ExecutionMonitor plan(Identifier tid, GraphService service) {
    final Graph graph = service.getCurrentGraph(tid);
    final TaskGraph taskGraph = transformer.transform(graph);
    return executor.createPlan(taskGraph);
  }

  @Override
  public ExecutionMonitor execute(Identifier tid, GraphService service) {
    return execute(tid, service, Tasks.context());
  }

  @Override
  public ExecutionMonitor execute(Identifier tid, GraphService service, TaskContext context) {
    final Graph graph = service.getCurrentGraph(tid);
    final TaskGraph taskGraph = transformer.transform(graph);
    return executor.run(taskGraph, context);
  }
}
