package io.sunshower.service.hal.core;

import io.sunshower.common.Identifier;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

class VertexTest extends SerializationTestCase {

  public VertexTest() {
    super(SerializationAware.Format.JSON, Vertex.class);
  }

  @Test
  void ensureVertexReferenceIsCopiedCorrectly() {
    Identifier id = Identifier.random();
    Vertex v = new Vertex();
    Reference ref = new Reference();
    ref.setNamespace("test");
    ref.setReferenceType(Vertex.class);
    ref.setTargetId(id);
    ref.setTargetKey("key");
    v.setReference(ref);
    Vertex copy = copy(v);
    assertThat(copy.getReference(), is(not(nullValue())));
  }
}
