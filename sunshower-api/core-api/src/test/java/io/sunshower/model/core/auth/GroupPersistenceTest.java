package io.sunshower.model.core.auth;

import io.sunshower.model.core.PersistenceTest;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

public class GroupPersistenceTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureGroupCanBePersisted() {
    final Group group = new Group();
    group.setName("frapper");
    entityManager.persist(group);
    entityManager.flush();
  }
}
