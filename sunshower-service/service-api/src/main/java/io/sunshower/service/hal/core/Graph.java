package io.sunshower.service.hal.core;

import io.sunshower.common.Identifier;
import io.sunshower.common.rs.MapAdapter;
import io.sunshower.persist.Sequence;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.service.model.PropertyAwareObject;
import java.util.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "graph")
public class Graph extends PropertyAwareObject<Graph> {

  @XmlElement(name = "edges", type = Edge.class)
  private Set<Edge> edges;

  @XmlElement(name = "vertices", type = Vertex.class)
  private Set<Vertex> vertices;

  @XmlElement(name = "layout")
  private GlobalLayout layout;

  @XmlElement(name = "styles")
  @XmlJavaTypeAdapter(MapAdapter.class)
  private Map<String, Stylesheet> styles;

  public Graph() {
    super(Graph.class);
    setId(DistributableEntity.sequence.next());
  }

  public Set<Edge> getEdges() {
    return edges == null ? Collections.emptySet() : edges;
  }

  public void setEdges(Set<Edge> edges) {
    this.edges = edges;
  }

  public Set<Vertex> getVertices() {
    return vertices == null ? Collections.emptySet() : vertices;
  }

  public void setVertices(Set<Vertex> vertices) {
    this.vertices = vertices;
  }

  public void addEdge(Edge e) {
    if (edges == null) {
      edges = new HashSet<>();
    }
    edges.add(e);
  }

  public void addVertex(Vertex v) {
    if (vertices == null) {
      vertices = new HashSet<>();
    }
    vertices.add(v);
  }

  public void addStyle(String key, Stylesheet stylesheet) {
    if (styles == null) {
      styles = new HashMap<>();
    }
    styles.put(key, stylesheet);
  }

  @Override
  public Sequence<Identifier> getSequence() {
    return DistributableEntity.sequence;
  }
}
