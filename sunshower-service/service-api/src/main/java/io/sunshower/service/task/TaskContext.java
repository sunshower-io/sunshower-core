package io.sunshower.service.task;

import java.util.HashMap;
import java.util.Map;

public class TaskContext {

  private final Map<Object, Object> bindings;

  public TaskContext() {
    bindings = new HashMap<>();
  }

  public <T> Binding<T> bind(T key) {
    return new Binding<>(key);
  }

  public boolean containsBinding(Class<?> type) {
    return bindings.containsKey(type);
  }

  @SuppressWarnings("unchecked")
  public <U> U getBinding(Class<?> type) {
    return (U) bindings.get(type);
  }

  public class Binding<T> {
    final T key;

    public Binding(T key) {
      this.key = key;
    }

    public <U> TaskContext to(U value) {
      bindings.put(key, value);
      return TaskContext.this;
    }
  }
}
