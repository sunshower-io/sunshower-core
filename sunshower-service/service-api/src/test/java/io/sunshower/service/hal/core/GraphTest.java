package io.sunshower.service.hal.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.model.core.Property;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class GraphTest extends SerializationTestCase {

  public GraphTest() {
    super(
        SerializationAware.Format.JSON,
        Edge.class,
        Vertex.class,
        Properties.class,
        Layout.class,
        TestElement.class,
        Graph.class);
  }

  @Test
  public void ensureWritingRefWorks() {}

  @Test
  public void ensureReadingFromDockerProduces() {
    final Graph g = new Graph();
    Vertex fst = createVertex("fst");
    fst.addContent(new Content(ContentType.Reference, "frapper"));
    Vertex snd = createVertex("snd");
    Edge fstToSnd = createEdge(fst, snd);
    g.addVertex(fst);
    g.addVertex(snd);
    g.addEdge(fstToSnd);
    write(g, System.out);
    assertThat(copy(g).getEdges().size(), is(1));
  }

  @Test
  public void ensureWritingComplexElementToJsonWorks() {
    final Graph g = new Graph();
    Vertex fst = createVertex("fst");
    Vertex snd = createVertex("snd");
    Edge fstToSnd = createEdge(fst, snd);
    g.addVertex(fst);
    g.addVertex(snd);
    g.addEdge(fstToSnd);
    write(g, System.out);
    assertThat(copy(g).getEdges().size(), is(1));
  }

  @Test
  public void ensureWritingVerticesProducesExpectedCount() {
    final Graph g = new Graph();
    Vertex fst = createVertex("fst");
    Vertex snd = createVertex("snd");
    Edge fstToSnd = createEdge(fst, snd);
    g.addVertex(fst);
    g.addVertex(snd);
    g.addEdge(fstToSnd);

    copy(g);
    //        assertThat(copy(g).getVertices().size(), is(2));
  }

  @Test
  public void ensureGraphIdIsNotNull() {
    final Graph g = new Graph();
    assertThat(g.getId(), is(copy(g).getId()));
    assertThat(g.getId(), is(not(nullValue())));
  }

  @Test
  public void ensureWritingComplexGraphWorks() {

    final Graph g = new Graph();
    Vertex fst = createVertex("fst");
    Vertex snd = createVertex("snd");
    Edge fstToSnd = createEdge(fst, snd);
    Vertex thrd = createVertex("third");
    Vertex fourth = createVertex("fourth");
    Edge thirdToFourth = createEdge(thrd, fourth);
    Edge fstToFourth = createEdge(fst, fourth);

    g.addEdge(fstToFourth);
    g.addEdge(fstToSnd);
    g.addEdge(thirdToFourth);

    g.addVertex(fst);
    g.addVertex(snd);
    g.addVertex(thrd);
    g.addVertex(fourth);

    assertThat(copy(g), is(g));
  }

  @NotNull
  private Edge createEdge(Vertex fst, Vertex snd) {
    Edge element = new Edge();
    element.addProperty(Property.string("cool", "Value", "beans"));
    element.getLayout().setWidth(100);
    element.getLayout().setHeight(100);
    element.getLayout().setX(235);
    element.getLayout().setY(235);
    element.setSource(fst);
    element.setTarget(snd);
    return element;
  }

  @NotNull
  private Vertex createVertex(String key) {
    Vertex element = new Vertex();
    element.addProperty(Property.string("whatever", "label", key));
    element.getLayout().setWidth(10);
    element.getLayout().setHeight(45);
    element.setStyle("color", "red");
    element.setStyle("stroke", "green");
    element.getLayout().setX(99);
    element.getLayout().setY(99);
    return element;
  }
}
