package io.sunshower.service.model.task;

import io.reactivex.ObservableSource;

import java.util.logging.Level;

public interface TaskLogger extends ObservableSource<TaskEvent> {
    
    void infof(String msg, Object...args);
    
    void warnf(String fmt, Object...args);
    
    void logf(Level level, String fmt, Object...args);
}
