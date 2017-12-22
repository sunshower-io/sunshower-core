package io.sunshower.service.task.exec;

import io.sunshower.common.Identifier;
import io.reactivex.subjects.Subject;
import io.sunshower.service.model.task.TaskEvent;
import io.sunshower.service.model.task.TaskLogger;
import io.sunshower.service.task.TaskContext;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

/**
 * Created by haswell on 2/15/17.
 */
class ParallelLevelSet implements LevelSet<TaskElement> {

    int order;
    CountDownLatch latch;
    volatile boolean last;


    private final TaskLogger taskLogger;
    private final TinkerGraph graph;


    private final TaskContext context;
    private final Identifier sequenceId;
    private final Identifier scheduleId;

    private final Subject<TaskEvent>       subject;
    private final ExecutorService          executorService;
    private final Map<String, TaskElement> elements;
    private final ApplicationContext       applicationContext;
    private final ParallelTaskExecutor     parallelTaskExecutor;

    ParallelLevelSet(
            final TaskLogger taskLogger,
            final TaskContext context,
            final Identifier scheduleId,
            final TinkerGraph graph,
            final Subject<TaskEvent> subject,
            final ExecutorService  executorService,
            final ApplicationContext applicationContext,
            final ParallelTaskExecutor parallelTaskExecutor
            ) {
        this.taskLogger = taskLogger;
        this.graph = graph;
        this.subject = subject;
        this.context = context;
        this.scheduleId = scheduleId;
        this.sequenceId = Identifier.random();
        this.elements = new LinkedHashMap<>();
        this.executorService = executorService;
        this.applicationContext = applicationContext;
        this.parallelTaskExecutor = parallelTaskExecutor;
    }

    @Override
    public boolean add(TaskElement element) {
        return elements.put(element.vertex.label(), element) == null;
    }

    @Override
    public int size() {
        return elements.size();
    }

    void run() {
        ParallelTaskExecutor.log.log(
                Level.INFO,
                "Beginning task set {0} on schedule {1}",
                new Object[]{sequenceId, scheduleId}
        );
        subject.onNext(new TaskEvent(
                TaskEvent.Type.SequenceBeginning,
                scheduleId,
                sequenceId
        ));
        doRun();
        if (last) {
            subject.onNext(new TaskEvent(
                    TaskEvent.Type.SequenceComplete,
                    scheduleId,
                    sequenceId)
            );

            ParallelTaskExecutor.log.log(
                    Level.INFO,
                    "Task set {0} on schedule {1} complete",
                    new Object[]{sequenceId, scheduleId}
            );
        }
    }

    private void doRun() {
        try {
            CountDownLatch latch = new CountDownLatch(size());
            for (TaskElement e : this) {
                executorService.submit(new TaskStep(
                        taskLogger,
                        context,
                        graph,
                        e,
                        scheduleId,
                        latch,
                        subject,
                        applicationContext,
                        parallelTaskExecutor
                ));
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            this.latch.countDown();
        }
    }

    @Override
    public Iterator<TaskElement> iterator() {
        return elements.values().iterator();
    }
}
