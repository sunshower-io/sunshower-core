package io.sunshower.model.core.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.PersistenceTest;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

class ObjectIdentityTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureDeletingObjectIdentityWorks() {
    final ObjectIdentity oid = new ObjectIdentity();
    oid.setObject(new SecuredObject(Void.class));
    oid.setReference(Identifier.random());
    entityManager.persist(oid);
    entityManager.flush();
    entityManager.remove(oid);
    ObjectIdentity objectIdentity = entityManager.find(ObjectIdentity.class, oid.getId());
    assertThat(objectIdentity, is(nullValue()));
  }
}
