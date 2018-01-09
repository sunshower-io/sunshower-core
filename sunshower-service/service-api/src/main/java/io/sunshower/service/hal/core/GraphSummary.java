package io.sunshower.service.hal.core;

import io.sunshower.common.Identifier;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 10/13/17. */
@XmlRootElement(name = "summary")
public class GraphSummary {

  @XmlElement(name = "vertices")
  private ElementSummary vertexSummary;

  @XmlElement(name = "edges")
  private ElementSummary edgeSummary;

  public ElementSummary getVertexSummary() {
    return vertexSummary;
  }

  public void setVertexSummary(ElementSummary vertexSummary) {
    this.vertexSummary = vertexSummary;
  }

  public ElementSummary getEdgeSummary() {
    return edgeSummary;
  }

  public void setEdgeSummary(ElementSummary edgeSummary) {
    this.edgeSummary = edgeSummary;
  }

  public boolean contains(Identifier nodeId) {
    return containsEdge(nodeId) || containsVertex(nodeId);
  }

  private boolean containsEdge(Identifier nodeId) {
    return edgeSummary != null && edgeSummary.contains(nodeId);
  }

  private boolean containsVertex(Identifier nodeId) {
    return vertexSummary != null && vertexSummary.contains(nodeId);
  }
}
