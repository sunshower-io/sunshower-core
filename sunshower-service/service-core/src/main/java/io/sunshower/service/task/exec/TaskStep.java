package io.sunshower.service.task.exec;

import io.reactivex.subjects.Subject;
import io.sunshower.common.Identifier;
import io.sunshower.service.model.task.TaskEvent;
import io.sunshower.service.model.task.TaskLogger;
import io.sunshower.service.task.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.javatuples.Pair;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/** Created by haswell on 2/15/17. */
class TaskStep implements Callable<ExecutionResult> {

  private final TaskContext context;
  private Identifier taskId;
  private final Identifier scheduleId;
  private final TinkerGraph graph;
  private final CountDownLatch latch;
  private final TaskElement taskElement;
  private final Subject<TaskEvent> subject;
  private ParallelTaskExecutor parallelTaskExecutor;
  private final ApplicationContext applicationContext;
  private final TaskLogger taskLogger;

  public TaskStep(
      TaskLogger logger,
      TaskContext context,
      TinkerGraph graph,
      TaskElement e,
      Identifier scheduleId,
      CountDownLatch latch,
      Subject<TaskEvent> subject,
      ApplicationContext applicationContext,
      ParallelTaskExecutor parallelTaskExecutor) {
    this.taskLogger = logger;
    this.graph = graph;
    this.latch = latch;
    this.taskElement = e;
    this.subject = subject;
    this.context = context;
    this.scheduleId = scheduleId;
    this.applicationContext = applicationContext;
    this.parallelTaskExecutor = parallelTaskExecutor;
    ParallelTaskExecutor.log.log(Level.INFO, "Initializing task step for task ", scheduleId);
  }

  final Map<Class<?>, Object> createInjectionContext(Class<?> type, Object value) {
    final Map<Class<?>, Object> result = new HashMap<>();
    result.put(TaskLogger.class, taskLogger);
    fromPreconditions(type, value, result);
    return result;
  }

  private void fromPreconditions(Class<?> type, Object value, Map<Class<?>, Object> result) {
    ParallelTaskExecutor.log.info("Computing preconditions...");
    if (type.isAnnotationPresent(Preconditions.class)) {
      final Pair<Class<?>, Object>[] preconditions =
          compute(type.getAnnotation(Preconditions.class));
      for (Pair<Class<?>, Object> precondition : preconditions) {
        result.put(precondition.getValue0(), precondition.getValue1());
        runPrecondition(precondition.getValue0(), precondition.getValue1(), result);
      }
    }
    ParallelTaskExecutor.log.info("Preconditions computed");
  }

  private void runPrecondition(Class<?> value0, Object value1, Map<Class<?>, Object> result) {
    Object r = runRuns(value0, value1);
    if (r != null) {
      result.put(r.getClass(), r);
    }
  }

  private Pair<Class<?>, Object>[] compute(Preconditions annotation) {
    Precondition[] value = annotation.value();
    @SuppressWarnings("unchecked")
    final Pair<Class<?>, Object>[] results = new Pair[value.length];
    Arrays.sort(value, (lhs, rhs) -> lhs.order() < rhs.order() ? -1 : 1);
    for (int i = 0; i < value.length; i++) {
      results[i] = Pair.with(value[i].condition(), createPrecondition(value[i].condition()));
    }
    return results;
  }

  private Object createPrecondition(Class<?> condition) {
    return applicationContext
        .getAutowireCapableBeanFactory()
        .createBean(condition, AutowireCapableBeanFactory.AUTOWIRE_NO, true);
  }

  private Identifier resolveId() {
    if (taskId != null) {
      return taskId;
    }

    final Object id = taskElement.vertex.id();
    try {
      return (taskId = Identifier.decode((String) id));
    } catch (Exception ex) {
      return (taskId = Identifier.random());
    }
  }

  public ExecutionResult<?> call() {
    ParallelTaskExecutor.log.info("Beginning task execution");
    subject.onNext(new TaskEvent(TaskEvent.Type.TaskBeginning, scheduleId, taskId));
    try {
      Object result = doRun();
      subject.onNext(new TaskEvent(TaskEvent.Type.TaskBeginning, scheduleId, taskId));
      return new ExecResult(result);
    } catch (Exception e) {
      ParallelTaskExecutor.log.log(
          Level.WARNING,
          "Failed to execute task {0} in plan {1}.  " + "Reason: {2}.  Full trace at debug",
          new Object[] {resolveId(), scheduleId, e.getMessage()});
      e.printStackTrace();
      subject.onError(new TaskException(e, scheduleId, taskId));
      return new ExecResult(e);
    } finally {
      latch.countDown();
    }
  }

  private Object doRun() {
    final VertexProperty<Object> property = taskElement.vertex.property("descriptor");
    final ElementDescriptor descriptor = (ElementDescriptor) property.value();
    prepare(descriptor);
    final Class<?> type = descriptor.getType();
    final Object value = descriptor.getInstance();
    inject(type, value);
    runPostConstructs(type, value);
    runBefores(type, value);
    Object result = runRuns(type, value);
    if (result != null) {
      taskElement.vertex.property("result", result);
    }
    runAfters(type, value);
    return result;
  }

  @SuppressWarnings("all")
  private void prepare(ElementDescriptor descriptor) {
    final InjectionContext injectionContext =
        new InjectionContext(context, descriptor, graph, applicationContext);
    descriptor.setInstance(injectionContext.invoke());
  }

  private void runPostConstructs(Class<?> type, Object value) {
    runWithAnnotation(type, value, PostConstruct.class);
  }

  private Object runWithAnnotation(
      Class<?> type, Object value, Class<? extends Annotation> annotation) {
    Object result = null;

    Set<Method> methods = new HashSet<>();
    Class<?> proxyType = value.getClass();
    for (Method m : type.getMethods()) {
      if (m.isAnnotationPresent(annotation) && !methods.contains(m)) {
        try {
          final Method proxyMethod = proxyType.getMethod(m.getName());
          proxyMethod.setAccessible(true);
          Object r = proxyMethod.invoke(value);
          if (r != null) {
            result = r;
          }
          methods.add(m);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }

  private void runBefores(Class<?> type, Object value) {
    runWithAnnotation(type, value, Before.class);
  }

  private Object runRuns(Class<?> type, Object value) {
    return runWithAnnotation(type, value, Run.class);
  }

  private Object runAfters(Class<?> type, Object value) {
    return runWithAnnotation(type, value, After.class);
  }

  private void inject(Class<?> objectype, Object element) {

    runAndInjectPreconditions(objectype, element);

    findAndInjectProperties(objectype, element);
  }

  private void findAndInjectProperties(Class<?> objectype, Object element) {
    //        final Identifier id = resolveId();
    //        GraphTraversal<
    //                Vertex,
    //                Vertex
    //                > next = this.graph.traversal().V(this.taskElement.vertex.id()).out();
    //        if (next.hasNext()) {
    //            Vertex v = next.next();
    //            try {
    //                VertexProperty<Object> result = v.property("result");
    //                if (result != null && result.value() != null) {
    //                    final Object value = result.value();
    //                    injectProperties(objectype, element, value);
    //                }
    //            }
    //            catch (IllegalStateException ex) {
    //                ParallelTaskExecutor.log.log(Level.WARNING, "Caught unexpected exception while
    // processing result", ex);
    //            }
    //        }
  }

  private void runAndInjectPreconditions(Class<?> objectype, Object element) {
    Map<Class<?>, Object> injectionContext = createInjectionContext(objectype, element);
    for (Class<?> type = objectype; type != null; type = type.getSuperclass()) {
      for (Field f : type.getDeclaredFields()) {
        if (f.isAnnotationPresent(Context.class)) {
          f.setAccessible(true);
          Object value = injectionContext.get(f.getType());
          if (value != null) {
            try {
              f.set(element, value);
            } catch (IllegalAccessException e) {
              ParallelTaskExecutor.log.log(
                  Level.WARNING,
                  "Failed to set field {0} on type {1}." + "  Did you declare it final?",
                  new Object[] {f.getName(), objectype.getName()});
            }
          }
        }
      }
    }
  }

  private void injectProperties(Class<?> objectype, Object element, Object value) {
    for (Class<?> type = objectype; type != null; type = type.getSuperclass()) {
      for (Field f : type.getDeclaredFields()) {
        if (f.isAnnotationPresent(Property.class)) {
          f.setAccessible(true);
          try {
            f.set(element, value);
          } catch (IllegalAccessException e) {
            ParallelTaskExecutor.log.log(
                Level.WARNING,
                "Failed to set field {0} on type {1}." + "  Did you declare it final?",
                new Object[] {f.getName(), objectype.getName()});
          }
        }
      }
    }
  }
}
