package io.sunshower.service.task;

public interface ElementContext {
  <T> void transform(T graph, Class<T> type);

  void register(String key, Class<?> type);

  <T> ElementDescriptor<T> resolve(Node v, TaskGraph taskGraph, ContextResolver resolver);

  Relationship resolveRelationship(Edge edge, TaskGraph result);
}
