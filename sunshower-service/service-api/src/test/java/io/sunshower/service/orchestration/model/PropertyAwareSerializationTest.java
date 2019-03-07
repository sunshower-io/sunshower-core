package io.sunshower.service.orchestration.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.model.core.Property;
import io.sunshower.model.core.PropertyAwareObject;
import io.sunshower.service.hal.core.Graph;
import io.sunshower.service.hal.core.Vertex;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import org.junit.jupiter.api.Test;

public class PropertyAwareSerializationTest extends SerializationTestCase {

  public PropertyAwareSerializationTest() {
    super(
        SerializationAware.Format.XML,
        new Class<?>[] {Graph.class, Property.class, PropertyAwareObject.class});
  }

  @Test
  public void ensureWritingGraphWorks() {
    final Graph g = new Graph();
    g.setName("frapper");
    Vertex v = new Vertex();
    v.setName("frapperv");
    g.addVertex(v);
    final Graph copy = copy(g);
    assertThat(copy.getVertices().size(), is(1));
  }

  @Test
  public void ensureAddingStringPropertyToGraphWorks() {
    final Graph g = new Graph();
    g.setName("frapper");

    g.addProperty(new Property(Property.Type.String, "hello", "world", "cool"));
    write(g, System.out);
    assertThat(copy(g).getProperties().size(), is(1));
  }
}
