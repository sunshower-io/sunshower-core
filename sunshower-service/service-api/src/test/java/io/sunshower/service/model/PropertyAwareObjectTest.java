package io.sunshower.service.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.sunshower.service.PersistTestCase;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import persist.test.TestPropertyEntity;

class PropertyAwareObjectTest extends PersistTestCase {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureSavingTestPropertyWorks() {
    TestPropertyEntity e = new TestPropertyEntity();
    e.addProperty(Property.string("hello", "world", "how"));
    entityManager.persist(e);
    entityManager.flush();
    e = entityManager.find(TestPropertyEntity.class, e.getId());
    assertThat(e.getProperty("hello").getValue(), is("how"));
  }

  @Test
  void ensureSavingRoleWorks() {

    TestPropertyEntity e = new TestPropertyEntity();
    e.setRole(TestPropertyEntity.class);
    e.setProperty(TestPropertyEntity.class, "hello");
    entityManager.persist(e);
    entityManager.flush();

    e = entityManager.find(TestPropertyEntity.class, e.getId());
    assertThat(e.getProperty(TestPropertyEntity.class).getValue(), is("hello"));
    assertEquals(TestPropertyEntity.class, e.getRole());
  }
}
