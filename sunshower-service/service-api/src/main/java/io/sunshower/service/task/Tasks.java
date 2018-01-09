package io.sunshower.service.task;

import io.sunshower.common.Identifier;
import io.sunshower.persistence.core.DistributableEntity;
import java.util.*;

/** Created by haswell on 3/26/17. */
public class Tasks {

  public static TaskContext context() {
    return new TaskContext();
  }

  public static <T> TaskContext.Binding<T> bind(T key) {
    return context().bind(key);
  }

  public static TaskGraphBuilder newGraph() {
    return new TaskGraphBuilder();
  }

  public static Node find(TaskGraph graph, String name) {
    for (Node node : graph.getNodes()) {
      if (name.equals(node.getName())) {
        return node;
      }
    }
    throw new NoSuchElementException(String.format("No node named %s was found", name));
  }

  private static class Dependency {
    final String dependentName;
    final String dependencyName;

    private Dependency(String dependentName, String dependencyName) {
      this.dependentName = dependentName;
      this.dependencyName = dependencyName;
    }
  }

  public static class TaskGraphBuilder {
    private final String name;
    private final Identifier id;

    private final Map<String, Node> namedNodes;
    private final Map<Identifier, Node> nodes;
    private final Stack<String> currentNodeNames;
    private final Map<String, Set<Dependency>> dependencies;

    public TaskGraphBuilder() {
      this(DistributableEntity.sequence.next().toString(), DistributableEntity.sequence.next());
    }

    public TaskGraphBuilder(String name, Identifier id) {
      this.id = id;
      this.name = name;
      this.nodes = new HashMap<>();
      this.namedNodes = new HashMap<>();
      this.dependencies = new HashMap<>();
      this.currentNodeNames = new Stack<>();
    }

    public TaskGraphBuilder withValue(Object value) {
      final String current = currentNodeNames.peek();
      Node node = this.namedNodes.get(current);
      node.setValue(value);
      return this;
    }

    public TaskGraphBuilder bind(String s) {
      final String current = currentNodeNames.peek();
      Node node = this.namedNodes.get(current);
      node.setKey(s);
      return this;
    }

    public TaskGraphBuilder toContext(Object value) {
      return withValue(value);
    }

    public TaskGraphBuilder named(String name) {
      this.currentNodeNames.push(name);
      return new TaskGraphBuilder(name, DistributableEntity.sequence.next());
    }

    public TaskGraphBuilder dependsOn(String... names) {
      if (currentNodeNames.isEmpty()) {
        throw new IllegalArgumentException("You must specify a task somewhere to depend on");
      }
      final String current = currentNodeNames.pop();
      for (String dependency : names) {
        this.dependencies
            .computeIfAbsent(current, d -> new HashSet<>())
            .add(new Dependency(dependency, current));
      }
      return this;
    }

    public TaskGraphBuilder task(String name) {
      if (!namedNodes.containsKey(name)) {
        Identifier id = DistributableEntity.sequence.next();
        final Node node = new Node();
        node.setId(id);
        node.setName(name);
        nodes.put(id, node);
        namedNodes.put(name, node);
      }
      this.currentNodeNames.push(name);
      return this;
    }

    public TaskGraph create() {
      final TaskGraph taskGraph = new TaskGraph();

      Iterable<Dependency> deps =
          () -> dependencies.values().stream().flatMap(Collection::stream).iterator();

      for (Dependency dep : deps) {
        Node dependent = namedNodes.get(dep.dependentName);
        if (dependent == null) {
          task(dep.dependentName);
          dependent = namedNodes.get(dep.dependencyName);
        }
        Node dependency = namedNodes.get(dep.dependencyName);

        if (dependency == null) {
          task(dep.dependencyName);
          dependency = namedNodes.get(dep.dependencyName);
        }
        final Edge edge = new Edge();
        edge.setId(DistributableEntity.sequence.next());
        edge.setSource(dependent.getId());
        edge.setTarget(dependency.getId());
        taskGraph.addEdge(edge);
      }

      for (Node node : namedNodes.values()) {
        taskGraph.addNode(node);
      }
      return taskGraph;
    }
  }
}
