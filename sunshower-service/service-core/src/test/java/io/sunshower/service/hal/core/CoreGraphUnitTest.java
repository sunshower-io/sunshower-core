package io.sunshower.service.hal.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.sunshower.model.core.Property;
import io.sunshower.service.git.GitRepositoryTestCase;
import io.sunshower.test.common.SerializationAware;
import org.junit.Test;

public class CoreGraphUnitTest extends GitRepositoryTestCase {

  public CoreGraphUnitTest() {
    super(
        SerializationAware.Format.JSON,
        new Class<?>[] {Graph.class, Edge.class, Vertex.class, Property.class});
  }

  @Test
  public void ensureSavingComplexTopologicallyOrderedGraphWorks() {

    final Vertex five = newVertex("five");
    final Vertex seven = newVertex("seven");
    final Vertex three = newVertex("three");
    final Vertex eleven = newVertex("eleven");
    final Vertex eight = newVertex("eight");
    final Vertex two = newVertex("two");
    final Vertex nine = newVertex("nine");
    final Vertex ten = newVertex("ten");

    five.addProperty(Property.string("frapper", "Dapper", "cool"));

    final Graph g = new Graph();
    g.addVertex(five);
    g.addVertex(seven);
    g.addVertex(three);
    g.addVertex(eleven);
    g.addVertex(eight);
    g.addVertex(two);
    g.addVertex(nine);
    g.addVertex(ten);

    g.addEdge(edgeBetween(five, eleven));
    g.addEdge(edgeBetween(seven, eleven));
    g.addEdge(edgeBetween(three, eight));
    g.addEdge(edgeBetween(eleven, two));
    g.addEdge(edgeBetween(eleven, nine));
    g.addEdge(edgeBetween(eight, nine));
    g.addEdge(edgeBetween(eleven, ten));
    g.addEdge(edgeBetween(three, ten));

    Vertex fiv =
        copy(g).getVertices().stream().filter(t -> t.getName().equals("five")).findFirst().get();
    assertThat(fiv.getProperties().size(), is(1));
    assertThat(fiv.getProperties().get(0).getValue(), is("cool"));
  }

  //    @Test
  //    public void ensureSavingGraphToDatabaseWorks() {
  //        repository.open();
  //        repository.write("orchestration.hsf", input("graphs/sample-graph-simple.json"));
  //        repository.commit();
  //        Graph read = read(repository.read("orchestration.hsf"), Graph.class);
  //        assertThat(read.getEdges().size(), is(3));
  //        assertThat(read.getVertices().size(), is(4));
  //    }
  //
  private Edge edgeBetween(Vertex source, Vertex target) {
    final Edge edge = new Edge();
    edge.setSource(source);
    edge.setTarget(target);
    return edge;
  }

  private Vertex newVertex(String name) {
    final Vertex vertex = new Vertex();
    vertex.setName(name);
    final Layout layout = new Layout();
    layout.setHeight(200);
    layout.setHeight(400);
    layout.setX(400);
    layout.setY(400);
    vertex.setLayout(layout);
    return vertex;
  }
}
