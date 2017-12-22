package io.sunshower.service.task.exec;

import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.sunshower.service.model.task.TaskEvent;
import io.sunshower.service.model.task.TaskLogger;

import java.util.logging.Level;

class RxTaskLogger implements TaskLogger {
    
   
    final Subject<TaskEvent> subject;
    
    public RxTaskLogger(ParallelTaskExecutor.ParallelTask parallelTask) {
        subject = PublishSubject.create();
    }

    @Override
    public void infof(String msg, Object... args) {
        subject.onNext(new TaskLogEvent(Level.INFO, String.format(msg, args)));
    }

    @Override
    public void warnf(String fmt, Object... args) {
        subject.onNext(new TaskLogEvent(Level.WARNING, String.format(fmt, args)));

    }

    @Override
    public void logf(Level level, String fmt, Object... args) {
        subject.onNext(new TaskLogEvent(Level.INFO, String.format(fmt, args)));

    }

    @Override
    public void subscribe(Observer<? super TaskEvent> observer) {
        subject.subscribe(observer);
    }
}
