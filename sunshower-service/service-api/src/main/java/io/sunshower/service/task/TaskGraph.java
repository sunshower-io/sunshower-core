package io.sunshower.service.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 3/26/17. */
@XmlRootElement(name = "task-graph")
public class TaskGraph {

  @XmlElement(name = "node")
  @XmlElementWrapper(name = "nodes")
  private Set<Node> nodes;

  @XmlElement(name = "edges")
  @XmlElementWrapper(name = "edges")
  private Set<Edge> edges;

  @XmlAttribute private String format;

  public Set<Node> getNodes() {
    if (nodes == null) {
      return Collections.emptySet();
    }
    return nodes;
  }

  public void setNodes(Set<Node> nodes) {
    this.nodes = nodes;
  }

  public Set<Edge> getEdges() {
    if (edges == null) {
      return Collections.emptySet();
    }
    return edges;
  }

  public void setEdges(Set<Edge> edges) {
    this.edges = edges;
  }

  public void addNode(Node node) {
    if (nodes == null) {
      nodes = new HashSet<>();
    }
    nodes.add(node);
  }

  public void addEdge(Edge edge) {
    if (edges == null) {
      edges = new HashSet<>();
    }
    edges.add(edge);
  }

  public String getFormat() {
    return format;
  }
}
