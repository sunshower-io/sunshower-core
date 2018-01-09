package io.sunshower.service.model;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class PropertyGraphElement<G extends PropertyGraph>
    extends PropertyAwareObject<PropertyGraphElement<G>> {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "graph_id")
  private G graph;

  @SuppressWarnings("unchecked")
  protected PropertyGraphElement(Class<G> type) {
    super((Class) type);
  }

  public G getGraph() {
    return graph;
  }

  public void setGraph(G graph) {
    this.graph = graph;
  }
}
