package io.sunshower.service.task.exec;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.sunshower.common.Identifier;
import io.sunshower.common.rs.IdentifierConverter;
import io.sunshower.service.ServiceTestCase;
import io.sunshower.service.model.task.*;
import io.sunshower.service.task.*;
import io.sunshower.test.common.SerializationAware;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParallelTaskExecutorTest extends ServiceTestCase {

  @Inject private ElementContext context;

  @Inject private ParallelTaskExecutor executor;

  private TaskGraph taskGraph;

  static {
    ParallelTaskExecutor.log.setLevel(Level.FINEST);
  }

  public ParallelTaskExecutorTest() {
    super(
        SerializationAware.Format.JSON,
        new Class[] {
          TaskGraph.class, SampleNode.class, ExecutionPlan.class, IdentifierConverter.class
        });
  }

  @BeforeEach
  public void setUp() throws InterruptedException {

    taskGraph =
        Tasks.newGraph()
            .named("my-test")
            .task("2")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("2"))
            .dependsOn("11")
            .task("9")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("9"))
            .dependsOn("11", "8")
            .task("10")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("10"))
            .dependsOn("11", "3")
            .task("11")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("11"))
            .dependsOn("7", "5")
            .task("8")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("8"))
            .dependsOn("3", "7")
            .task("3")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("3"))
            .task("7")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("7"))
            .task("14")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("14"))
            .dependsOn("2")
            .task("5")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("5"))
            .task("13")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("13"))
            .dependsOn("2")
            .create();
  }

  @Test
  public void ensureLoggingWorks() throws ExecutionException, InterruptedException {
    for (int j = 0; j < 10; j++) {

      List<String> list = new ArrayList<>();

      ExecutionMonitor plan = executor.run(taskGraph);
      TaskLogger logger = plan.getLogger();
      final AtomicInteger i = new AtomicInteger();
      logger.subscribe(
          new Observer<TaskEvent>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(TaskEvent taskEvent) {
              list.add(taskEvent.toString());
              i.incrementAndGet();
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {}
          });
      plan.join();

      assertThat(i.get() > 0, is(true));
    }
  }

  @Test
  public void ensureCompletableFutureWorks() throws ExecutionException, InterruptedException {
    ExecutionMonitor plan = executor.run(taskGraph);
    plan.toFuture().get();
  }

  @Test
  public void ensureTaskPlanGetsComputed() throws InterruptedException {
    ExecutionPlan plan = executor.createPlan(taskGraph).getExecutionPlan();
    ExecutionMonitor run = executor.run(plan);
    run.join();
  }

  @Test
  public void ensureSimpleGraphWorks() throws InterruptedException {

    ExecutionMonitor run = executor.run(taskGraph, new TaskContext().bind(int.class).to(101));
    run.join();
    TaskDefinition resolve = run.resolve(Tasks.find(taskGraph, "2").getId());
    SampleTask task = resolve.getInstance();
    assertThat(task.value, is(101));
  }

  @Test
  public void ensureComplexGraphExecutes() throws InterruptedException {

    long l1 = System.currentTimeMillis();

    ExecutionMonitor run = executor.run(taskGraph);
    System.out.println(run.getExecutionPlan());
    write(run.getExecutionPlan(), System.out);
    run.join();

    long l2 = System.currentTimeMillis();
    System.out.println("Time: " + (l2 - l1) / 1000);
  }

  @Test
  public void ensureAllTasksAreRegistered() {
    Arrays.asList("2", "11", "9", "8", "10", "3", "7", "5")
        .stream()
        .forEach(
            t -> {
              Node node = Tasks.find(taskGraph, t);
            });
  }

  @Test
  public void ensureInjectingDoesNotInterleveForMultipleNodesOfTheSameType()
      throws InterruptedException {

    TaskGraph graph =
        Tasks.newGraph()
            .task("A")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("A"))
            .task("B")
            .bind("io::sunshower::successor-task")
            .toContext(new SuccessorSampleNode())
            .dependsOn("A")
            .task("C")
            .bind("io::sunshower::successor-task")
            .toContext(new SuccessorSampleNode())
            .create();

    ExecutionMonitor run = executor.run(graph);
    run.join();
    Node c = Tasks.find(graph, "C");
    TaskDefinition resolve = run.resolve(c.getId());
    SuccessorSampleTask task = resolve.getInstance();
    assertThat(task.getPredecessor(), is(nullValue()));

    c = Tasks.find(graph, "B");
    resolve = run.resolve(c.getId());
    task = resolve.getInstance();
    assertThat(task.predecessor, is(not(nullValue())));
  }

  @Test
  public void ensureCloudFormationExampleLooksCorrect() {}

  @Test
  public void ensureInjectingMultiplePredecessorsIntoSuccessorWorks() throws InterruptedException {

    TaskGraph graph =
        Tasks.newGraph()
            .task("A")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("A"))
            .task("B")
            .bind("io::sunshower::successor-task")
            .toContext(new SuccessorSampleNode())
            .dependsOn("A")
            .task("C")
            .bind("io::sunshower::tertiary-task")
            .toContext(new TertiaryNode())
            .dependsOn("B")
            .create();

    assertThat(graph.getNodes().size(), is(3));

    ExecutionMonitor run = executor.run(graph);
    System.out.println(run.getExecutionPlan().toString());
    run.join();
    Node n = Tasks.find(graph, "C");
    TaskDefinition resolve = run.resolve(n.getId());
    TertiaryDependent dependent = resolve.getInstance();
    assertThat(dependent.getNode(), is(not(nullValue())));
    assertThat(dependent.getAncestor(), is(not(nullValue())));
    assertThat(dependent.getAncestor().getName(), is("A"));
  }

  @Test
  public void ensureInjectingPredecessorIntoSuccessorWorks() throws InterruptedException {

    TaskGraph graph =
        Tasks.newGraph()
            .task("A")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("A"))
            .task("B")
            .bind("io::sunshower::successor-task")
            .toContext(new SuccessorSampleNode())
            .dependsOn("A")
            .create();

    ExecutionMonitor run = executor.run(graph);
    run.join();

    Node b = Tasks.find(graph, "B");

    TaskDefinition resolve = run.resolve(b.getId());

    assertThat(resolve, is(not(nullValue())));
    final SuccessorSampleTask task = resolve.getInstance();
    final SampleNode predecessor = task.getPredecessor();
    assertThat(predecessor, is(not(nullValue())));
    assertThat(predecessor.getName(), is("A"));
  }

  @Test
  public void ensureBindingsWork() {

    TaskGraph graph =
        Tasks.newGraph()
            .task("A")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("A"))
            .task("B")
            .bind("io::sunshower::successor-task")
            .toContext(new SuccessorSampleNode())
            .dependsOn("A")
            .create();

    Node a = Tasks.find(graph, "A");
    assertThat(a.getKey(), is("io::sunshower::sample-task"));
    Node b = Tasks.find(graph, "B");
    assertThat(b.getKey(), is("io::sunshower::successor-task"));
  }

  @Test
  public void ensureSingleTaskWithNoDependenciesHasIdInjected() throws InterruptedException {
    TaskGraph graph =
        copy(
            Tasks.newGraph()
                .task("B")
                .bind("io::sunshower::sample-task")
                .toContext(new SampleNode("B"))
                .create());

    ExecutionMonitor run = executor.run(graph);
    Node a = Tasks.find(graph, "B");
    run.join();
    TaskDefinition task = run.resolve(a.getId());
    assertThat(task.getName(), is("B"));

    SampleTask instance = task.getInstance();
    assertThat(instance, is(not(nullValue())));
    assertThat(instance.getNode(), is(not(nullValue())));
  }

  @Test
  public void ensureSingleTaskWithNoDependenciesHasNodeInjected() throws InterruptedException {
    TaskGraph graph = createTaskGraph("E");
    ExecutionMonitor run = executor.run(graph);
    Node a = Tasks.find(graph, "E");
    run.join();
    TaskDefinition task = run.resolve(a.getId());
    assertThat(task.getName(), is("E"));
    SampleTask instance = task.getInstance();
    assertThat(instance, is(not(nullValue())));
    assertThat(instance.getNode(), is(not(nullValue())));
    SampleNode node = instance.getNode();
    assertThat(node.getName(), is("E"));
  }

  @Test
  public void ensureSingleTaskWithNoDependenciesHasNameInjected() throws InterruptedException {
    TaskGraph graph = createTaskGraph("A");
    ExecutionMonitor run = executor.run(graph);
    Node a = Tasks.find(graph, "A");
    run.join();
    TaskDefinition task = run.resolve(a.getId());
    assertThat(task.getName(), is("A"));
    SampleTask instance = task.getInstance();
    assertThat(instance, is(not(nullValue())));
    assertThat(instance.getNode(), is(not(nullValue())));
    assertThat(instance.getName(), is("A"));
  }

  @Test
  public void ensureSubmittingTasksWorks() {

    TaskGraph graph =
        Tasks.newGraph()
            .task("A")
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode("A"))
            .task("B")
            .bind("io::sunshower::successor-task")
            .toContext(new SuccessorSampleNode())
            .dependsOn("A")
            .create();

    final Identifier id = Tasks.find(graph, "A").getId();

    ExecutionMonitor run = executor.run(graph);
    TaskDefinition task = run.resolve(id);
    assertThat(task.getName(), is("A"));
  }

  @Test
  public void ensureTaskExecutionWorks() {
    final TaskGraph copy = copy(taskGraph);
    boolean found = false;
    for (Node node : copy.getNodes()) {
      if (node.getName().equals("2")) {
        SampleNode value = (SampleNode) node.getValue();
        assertThat(value.getName(), is("2"));
        found = true;
      }
    }
    assertTrue(found);
  }

  @PostConstruct
  public void postConstruct() {

    context.register("io::sunshower::successor-task", SuccessorSampleTask.class);

    context.register("io::sunshower::tertiary-task", TertiaryDependent.class);

    context.register("io::sunshower::sample-task", SampleTask.class);
  }

  private TaskGraph createTaskGraph(String c) {
    return copy(
        Tasks.newGraph()
            .task(c)
            .bind("io::sunshower::sample-task")
            .toContext(new SampleNode(c))
            .create());
  }
}
