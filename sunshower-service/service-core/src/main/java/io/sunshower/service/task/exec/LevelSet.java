package io.sunshower.service.task.exec;

/** Created by haswell on 2/4/17. */
public interface LevelSet<T> extends Iterable<T> {

  boolean add(T element);

  int size();
}
