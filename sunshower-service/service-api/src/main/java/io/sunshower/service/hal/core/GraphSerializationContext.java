package io.sunshower.service.hal.core;

import java.io.InputStream;

public interface GraphSerializationContext {

  Graph load(InputStream inputStream);

  <T> void registerComponent(Class<T> type);

  InputStream open(Graph graph);

  GraphSummary summarize(Graph graph);

  InputStream openSummary(Graph graph);

  GraphSummary loadSummary(InputStream read);
}
