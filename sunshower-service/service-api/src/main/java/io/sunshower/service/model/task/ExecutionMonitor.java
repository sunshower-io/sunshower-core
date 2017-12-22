package io.sunshower.service.model.task;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.sunshower.common.Identifier;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Created by haswell on 2/4/17.
 */
@XmlRootElement(name = "execution-monitor")
public interface ExecutionMonitor extends ObservableSource<TaskEvent> {

    void start();
    
    TaskLogger getLogger();

    Identifier getId();

    ExecutionPlan getExecutionPlan();

    TaskDefinition resolve(String name);

    TaskDefinition resolve(Identifier id);

    <T> T unwrap(Class<T> tinkerGraphClass);

    Iterable<TaskEvent> join() throws InterruptedException;
    
    CompletableFuture<Void> toFuture();
    
}
