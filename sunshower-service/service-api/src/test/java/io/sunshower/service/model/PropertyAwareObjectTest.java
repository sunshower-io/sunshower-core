package io.sunshower.service.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.sunshower.service.PersistTestCase;
import io.sunshower.service.model.properties.StringProperty;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import persist.test.TestPropertyEntity;

class PropertyAwareObjectTest extends PersistTestCase {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureSavingTestPropertyWorks() {
    TestPropertyEntity e = new TestPropertyEntity();
    e.addProperty(new StringProperty("hello", "world", "how"));
    entityManager.persist(e);
    entityManager.flush();
    e = entityManager.find(TestPropertyEntity.class, e.getId());
    assertThat(e.getProperty("hello").getValue(), is("how"));
  }
}
