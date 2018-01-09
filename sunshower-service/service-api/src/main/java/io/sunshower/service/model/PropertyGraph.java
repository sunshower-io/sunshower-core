package io.sunshower.service.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

@MappedSuperclass
public class PropertyGraph<
        V extends PropertyGraphElement<G>,
        E extends PropertyGraphElement<G>,
        G extends PropertyGraph<V, E, G>>
    extends PropertyAwareObject<G> {

  @OneToMany(
    mappedBy = "graph",
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private Set<E> edges;

  @OneToMany(
    mappedBy = "graph",
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private Set<V> vertices;

  protected PropertyGraph(Class<G> type) {
    super(type);
  }

  @SuppressWarnings("unchecked")
  public void addEdge(E edge) {
    if (edges == null) {
      edges = new HashSet<>();
    }
    if (edge != null) {
      edge.setGraph((G) this);
      edges.add(edge);
    }
  }

  @SuppressWarnings("unchecked")
  public void addVertex(V vertex) {
    if (vertices == null) {
      vertices = new HashSet<>();
    }
    if (vertex != null) {
      vertex.setGraph((G) this);
      vertices.add(vertex);
    }
  }

  public Set<E> getEdges() {
    return edges == null ? Collections.emptySet() : edges;
  }

  public void setEdges(Collection<E> edges) {
    this.edges = new HashSet<>(edges);
  }

  public Set<V> getVertices() {
    return vertices == null ? Collections.emptySet() : vertices;
  }

  public void setVertices(Collection<V> vertices) {
    this.vertices = new HashSet<>(vertices);
  }
}
