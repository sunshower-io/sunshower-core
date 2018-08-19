package io.sunshower.service.graph.service;

import io.sunshower.common.Identifier;
import io.sunshower.service.hal.core.Graph;
import io.sunshower.service.hal.core.GraphSummary;
import io.sunshower.service.hal.core.contents.ContentManager;
import io.sunshower.service.revision.model.Revision;

public interface GraphService {

  ContentManager contentManager(Identifier templateId);

  GraphSummary getSummary(Identifier id, Revision revision);

  GraphSummary getSummary(Identifier id);

  Graph getCurrentGraph(Identifier id);

  void saveGraph(Identifier id, Graph graph);

  Graph getGraph(Identifier id, Revision revision);
}
