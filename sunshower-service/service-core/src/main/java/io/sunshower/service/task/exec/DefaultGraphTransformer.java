package io.sunshower.service.task.exec;

import io.sunshower.common.Identifier;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.service.hal.core.Content;
import io.sunshower.service.hal.core.Edge;
import io.sunshower.service.hal.core.Graph;
import io.sunshower.service.hal.core.Vertex;
import io.sunshower.service.task.Node;
import io.sunshower.service.task.TaskGraph;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public class DefaultGraphTransformer implements GraphTransformer {


    @Override
    public TaskGraph transform(Graph graph) {

        Map<Identifier, Vertex> vertices = graph.getVertices()
                .stream().collect(
                        Collectors.toMap(
                                DistributableEntity::getId, identity())
                );
        Map<Identifier, Edge> edges = graph.getEdges()
                .stream().collect(
                        Collectors.toMap(
                                DistributableEntity::getId, identity())
                );
        TaskGraph taskGraph = new TaskGraph();

        for (Edge e : edges.values()) {
            io.sunshower.service.task.Edge edge = new io.sunshower.service.task.Edge();
            edge.setId(e.getId());
            Vertex source = e.getSource();
            if (source != null) {
                edge.setSource(source.getId());
            }

            Vertex target = e.getTarget();

            if (!e.getContents().isEmpty()) {
                for (Content c : e.getContents()) {
                    io.sunshower.service.task.Edge pseudoEdge = new io.sunshower.service.task.Edge();
                    pseudoEdge.setSource(e.getSource().getId());
                    pseudoEdge.setTarget(c.getId());
                    taskGraph.addEdge(pseudoEdge);

                    io.sunshower.service.task.Edge pseudoEdgeToVertex = new io.sunshower.service.task.Edge();
                    pseudoEdgeToVertex.setSource(pseudoEdge.getSource());
                    pseudoEdgeToVertex.setTarget(target.getId());
                    final Node pseudoVertex = new Node();
                    pseudoVertex.setId(target.getId());
                    taskGraph.addNode(pseudoVertex);
                }
            } else {
                edge.setTarget(target.getId());
                taskGraph.addEdge(edge);
            }

        }

        for (Vertex v : vertices.values()) {
            Node node = new Node();
            node.setId(v.getId());
            node.setName(v.getName());
            String elementProperty = v.getElementProperty("task-reference");
            if (elementProperty != null) {
                node.setKey(elementProperty);
            }
            node.setValue(v);

            if (!v.getContents().isEmpty()) {

                List<Edge> adjacencies = graph
                        .getEdges()
                        .stream()
                        .filter(t -> t.getSource()
                                .getId()
                                .equals(v.getId()))
                        .collect(Collectors.toList());
                for (Edge e : adjacencies) {

                    for (Content c : v.getContents()) {
                        final Node pseudoNode = new Node();
                        pseudoNode.setName(c.getName());
                        pseudoNode.setId(c.getId());
                        taskGraph.addNode(pseudoNode);
                        final io.sunshower.service.task.Edge vsp = new io.sunshower.service.task.Edge();
                        vsp.setSource(v.getId());
                        vsp.setTarget(pseudoNode.getId());
                        taskGraph.addEdge(vsp);

                        final io.sunshower.service.task.Edge pst = new io.sunshower.service.task.Edge();
                        pst.setSource(pseudoNode.getId());
                        pst.setTarget(e.getTarget().getId());
                        taskGraph.addEdge(pst);
                    }
                }
            }
            taskGraph.addNode(node);
        }

        return taskGraph;
    }
}
