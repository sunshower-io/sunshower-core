package io.sunshower.service.task.exec;

/**
 * Created by haswell on 2/4/17.
 */
public interface ParallelSchedule<T> extends Iterable<LevelSet<T>> {

    void add(LevelSet<T> set);

    LevelSet<T> get(int level);

    <T> T unwrap(Class<T> clazz);
}
