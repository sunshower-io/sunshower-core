package io.sunshower.service.graph.service;

public interface GraphServiceResolver {
  <T extends GraphService> T resolve(Class<T> type, String name);
}
