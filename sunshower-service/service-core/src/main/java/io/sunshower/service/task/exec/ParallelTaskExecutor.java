package io.sunshower.service.task.exec;

import io.reactivex.Observer;
import io.sunshower.common.Identifier;
import io.sunshower.persist.Identifiers;
import io.sunshower.persist.Sequence;
import io.sunshower.service.model.task.*;
import io.sunshower.service.task.*;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.javatuples.Triplet;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.tinkerpop.gremlin.process.traversal.P.eq;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

/**
 * Created by haswell on 2/4/17.
 */
public class ParallelTaskExecutor implements Thread.UncaughtExceptionHandler {

    public static final Logger log = Logger.getLogger(ParallelTaskExecutor.class.getName());

    static final Sequence<Identifier> sequence = Identifiers.randomSequence();

    public static final String TASK_STARTING      = "task-starting";
    public static final String TASK_COMPLETE      = "task-complete";
    public static final String TASK_ERROR         = "task-error";
    public static final String EXECUTION_STARTING = "exec-starting";
    public static final String EXECUTION_FINISHED = "exec-finished";


    private final ElementContext     elementContext;
    private final ExecutorService    executorService;
    private final ApplicationContext applicationContext;

    public ParallelTaskExecutor(
            ElementContext elementContext,
            ExecutorService executorService,
            ApplicationContext applicationContext
    ) {

        this.elementContext = elementContext;
        this.executorService = executorService;
        this.applicationContext = applicationContext;

        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public ExecutionMonitor run(ExecutionPlan plan) {
        ParallelTask task = (ParallelTask) plan.getPayload();
        executorService.submit(task);
        return task;
    }

    public ExecutionMonitor run(TaskGraph graph, TaskContext context) {

        final ParallelTask task = new ParallelTask(graph, context);
        executorService.submit(task);
        return task;
    }

    public ExecutionMonitor run(TaskGraph taskGraph) {
        final ParallelTask task = new ParallelTask(
                taskGraph,
                new TaskContext()
        );
        executorService.submit(task);
        return task;
    }

    public ExecutionMonitor createPlan(TaskGraph graph) {
        return new ParallelTask(
                graph,
                new TaskContext()
        );
    }


    private PSchedule createParallelSchedule(
            TaskContext context,
            TinkerGraph graph,
            TinkerGraph shadowGraph,
            TaskGraph taskGraph,
            Subject<TaskEvent> subject,
            Map<Identifier, Vertex> vertices,
            Identifier taskId,
            ParallelTask task
    ) {
        log.log(Level.INFO, "Computing schedule for request {0}", taskId);
        final GraphTraversalSource source = shadowGraph.traversal();

        GraphTraversal<
                Vertex,
                Vertex
                > group =
                source.V().where(inE().count().is(eq(0)));


        final PSchedule schedule = new PSchedule(task, taskId, subject, graph, taskGraph);
        Set<Identifier> ls       = new LinkedHashSet<>();
        boolean         running  = true;
        while (group.hasNext() && running) {
            LevelSet<TaskElement> set = createLevelSet(
                    group,
                    task.taskLogger,
                    context,
                    ls,
                    vertices,
                    graph,
                    schedule.id,
                    subject
            );
            schedule.add(set);
            Iterator<Identifier> ns = ls.iterator();
            if (!ns.hasNext()) {
                running = false;
            }
            Identifier   fst       = ns.next();
            Identifier[] remaining = new Identifier[ls.size() - 1];
            int          count     = 0;
            while (ns.hasNext()) {
                remaining[count++] = ns.next();
            }

            source.V().where(inE().count().is(eq(0)))
                    .and(hasId(fst, (Object[]) remaining)
                    ).drop().iterate();
            group = source.V().where(inE().count().is(eq(0)));
            ls.clear();
        }
        shadowGraph.clear();
        shadowGraph.close();
        log.info("Schedule computed");
        if (log.getLevel() == Level.INFO) {
            log.info(schedule.toString());
        }

        return schedule;
    }

    private LevelSet<TaskElement> createLevelSet(
            GraphTraversal<
                    Vertex,
                    Vertex
                    > group,
            TaskLogger logger,
            TaskContext context,
            Set<Identifier> ls,
            Map<Identifier, Vertex> vertices,
            TinkerGraph graph,
            Identifier scheduleId,
            final Subject<TaskEvent> subject
    ) {
        final LevelSet<TaskElement> set = new ParallelLevelSet(
                logger,
                context,
                scheduleId,
                graph,
                subject,
                executorService,
                applicationContext,
                this
        );
        while (group.hasNext()) {
            Vertex     t      = group.next();
            Identifier id     = (Identifier) t.id();
            Vertex     vertex = vertices.get(id);
            set.add(new TaskElement(vertex));
            ls.add((Identifier) t.id());
        }
        return set;
    }

    private Triplet<
            TinkerGraph,
            TinkerGraph,
            Map<Identifier, Vertex>
            > buildGraph(TaskGraph taskGraph
    ) {
        log.info("building internal graph from taskGraph");
        final TinkerGraph graph       = TinkerGraph.open();
        final TinkerGraph shadowGraph = TinkerGraph.open();
        final Map<Identifier,
                Vertex>
                vertices = new LinkedHashMap<>();

        log.info("Computing vertex structure...");
        final Map<Identifier,
                Vertex>
                shadowVertices = new LinkedHashMap<>();
        final ContextResolver resolver = new ExpressionContextResolver(graph);
        computeVertices(
                taskGraph,
                graph,
                shadowGraph,
                taskGraph,
                resolver,
                vertices,
                shadowVertices
        );

        log.info("Vertex structure complete");
        log.info("Computing edge structure");
        computeEdges(
                graph,
                shadowGraph,
                taskGraph,
                vertices,
                shadowVertices
        );
        log.info("Edge structure complete");
        log.info("Transforming graph...");
        elementContext.transform(graph, TinkerGraph.class);
        elementContext.transform(shadowGraph, TinkerGraph.class);
        log.info("Successfully transformed graph");
        return Triplet.with(graph, shadowGraph, vertices);
    }

    private void computeVertices(
            TaskGraph taskGraph,
            TinkerGraph graph,
            TinkerGraph shadowGraph,
            TaskGraph result,
            ContextResolver resolver,
            Map<Identifier, Vertex> vertices,
            Map<Identifier, Vertex> shadowVertices
    ) {
        for (Node v : result.getNodes()) {
            if (!vertices.containsKey(v.getId())) {
                final ElementDescriptor descriptor = elementContext.resolve(v, taskGraph, resolver);
                final String            name       = v.getName() != null ? v.getName() : v.getId().toString();
                final Vertex vertex =
                        graph.addVertex(
                                T.id,
                                v.getId(),
                                T.label, name
                        );
                Vertex shadowVertex = shadowGraph.addVertex(
                        T.id,
                        v.getId(),
                        T.label,
                        name
                );
                shadowVertices.put(v.getId(), shadowVertex);
                vertex.property("descriptor", descriptor);
                vertices.put(v.getId(), vertex);
                log.log(Level.INFO, "Vertex {0} computed: ", v.getId());
            }
        }
    }

    private void computeEdges(
            TinkerGraph graph,
            TinkerGraph shadowGraph,
            TaskGraph result,
            Map<Identifier, Vertex> vertices,
            Map<Identifier, Vertex> shadowVertices) {

        for (Edge edge : result.getEdges()) {

            Vertex shadowSource =
                    shadowVertices.computeIfAbsent(edge.getSource(),
                            k -> shadowGraph.addVertex(edge.getSource()));

            Vertex shadowTarget =
                    shadowVertices.computeIfAbsent(edge.getTarget(),
                            k -> shadowGraph.addVertex(edge.getTarget()));

            Vertex source =
                    vertices.computeIfAbsent(edge.getSource(),
                            k -> graph.addVertex(edge.getSource()));

            Vertex target =
                    vertices.computeIfAbsent(edge.getTarget(),
                            k -> graph.addVertex(edge.getTarget()));

            final String relationshipKey = elementContext
                    .resolveRelationship(edge, result).getKey();


            source.addEdge(relationshipKey, target);
            shadowSource.addEdge(relationshipKey, shadowTarget);
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();

    }


    private class PSchedule implements ParallelSchedule<TaskElement>, Runnable {
        final         Map<Integer, LevelSet<TaskElement>> levels;
        private final TinkerGraph                         graph;
        private int current = 0;
        private final    Identifier         id;
        private volatile boolean            running;
        private final    ExecutionPlan      tree;
        private          CountDownLatch     latch;
        private final    Subject<TaskEvent> subject;
        private final    TaskGraph          taskGraph;

        private PSchedule(ParallelTask task, Identifier taskId, Subject<TaskEvent> subject, TinkerGraph graph, TaskGraph taskGraph) {
            this.id = taskId;
            this.graph = graph;
            this.subject = subject;
            this.taskGraph = taskGraph;
            this.levels = new TreeMap<>();
            this.tree = new ExecutionPlan(id, task);
        }


        @Override
        public void add(LevelSet<TaskElement> set) {
            ParallelLevelSet pset = (ParallelLevelSet) set;
            pset.order = current;
            levels.put(current, pset);
            tree.addLevel(createLevel(current, set));
            current++;
        }

        private ExecutionLevel createLevel(int current, LevelSet<TaskElement> set) {
            final ExecutionLevel result = new ExecutionLevel();
            result.setLevel(current);
            int count = 0;
            for (TaskElement e : set) {
                Object id = e.vertex.id();
                String r  = id == null ? "" : id.toString();
                result.addTask(new ExecutionTask(count++, r, e.vertex.label()));
            }
            return result;
        }


        public ExecutionPlan getPlan() {
            return tree;
        }


        public void run() {
            try {
                this.running = true;
                this.latch = new CountDownLatch(levels.size());
                ParallelLevelSet last = null;
                for (LevelSet<TaskElement> set : this) {
                    final ParallelLevelSet pset = (ParallelLevelSet) set;
                    last = pset;
                    pset.latch = latch;
                    ((ParallelLevelSet) set).run();
                }
                if (last != null) {
                    last.last = true;
                }
                this.running = false;
            } finally {
                subject.onComplete();
            }
        }

        @Override
        public ParallelLevelSet get(int level) {
            return (ParallelLevelSet) levels.get(level);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T unwrap(Class<T> clazz) {
            return (T) graph;
        }


        public String toString() {
            return "ParallelSchedule{" +
                    "\n\texecution plan:\n" + showExecutionPlan()
                    + "\trunning: " + running + "\n"
                    + "}";
        }

        private String showExecutionPlan() {
            final StringBuilder b = new StringBuilder();
            for (LevelSet<TaskElement> t : this) {
                ParallelLevelSet pt = (ParallelLevelSet) t;
                b.append("\tlevel" + pt.order + ":").append("\n\t");
                int count = 0;
                for (TaskElement te : t) {
                    b.append("\t").append(te.vertex.label()).append(" ");
                    if (count++ < t.size() - 1) {
                        b.append("->").append(" ");
                    }
                }
                b.append("\n");

            }
            return b.toString();
        }

        @Override
        public Iterator<LevelSet<TaskElement>> iterator() {
            return levels.values().iterator();
        }
    }

    class ParallelTask implements ExecutionMonitor, Runnable {

        final Identifier taskId;

        final TaskContext        context;
        final TaskGraph          taskGraph;
        final Subject<TaskEvent> subject;
        final TaskLogger         taskLogger;

        final    PSchedule   schedule;
        volatile TinkerGraph graph;


        ParallelTask(TaskGraph taskGraph, TaskContext context) {
            this.context = context;
            this.taskLogger = new RxTaskLogger(this);
            this.taskId = sequence.next();
            this.taskGraph = taskGraph;
            this.subject = PublishSubject.create();
            this.schedule = createSchedule();
        }

        @Override
        public void run() {
            executorService.submit(schedule);
        }


        private PSchedule get() {
            return schedule;
        }

        private PSchedule createSchedule() {
            log.info("Creating schedule");
            Triplet<
                    TinkerGraph,
                    TinkerGraph,
                    Map<Identifier, Vertex>
                    > result =
                    buildGraph(taskGraph);
            this.graph = result.getValue0();

            return createParallelSchedule(
                    context,
                    result.getValue0(),
                    result.getValue1(),
                    taskGraph,
                    subject,
                    result.getValue2(),
                    taskId,
                    this
            );
        }

        @Override
        public Identifier getId() {
            return taskId;
        }

        @Override
        public void start() {
            run();
        }

        @Override
        public TaskLogger getLogger() {
            return taskLogger;
        }

        public Iterable<TaskEvent> join() throws InterruptedException {
            return subject.toList().blockingGet();
        }

        @Override
        @SuppressWarnings("unchecked")
        public CompletableFuture<Void> toFuture() {
            return CompletableFuture
                    .supplyAsync(() -> {
                                this.joinWithoutException();
                                return null;
                            },
                            executorService
                    );
        }


        void joinWithoutException() {
            try {
                join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @SuppressWarnings("unchecked")
        public <T> T unwrap(Class<T> type) {
            if (type == Subject.class) {
                return (T) subject;
            } else if (type == TinkerGraph.class) {
                return (T) get().graph;
            }
            return null;
        }

        @Override
        public ExecutionPlan getExecutionPlan() {
            return get().tree;
        }

        @Override
        public TaskDefinition resolve(String name) {
            return null;
        }

        @Override
        public TaskDefinition resolve(Identifier id) {
            ElementDescriptor<?> descriptor = (ElementDescriptor<?>) graph
                    .vertices(id)
                    .next()
                    .property("descriptor").value();
            return new TaskDefinition(descriptor);
        }


        @Override
        public void subscribe(Observer<? super TaskEvent> observer) {
            subject.subscribe(observer);
        }
    }

}
