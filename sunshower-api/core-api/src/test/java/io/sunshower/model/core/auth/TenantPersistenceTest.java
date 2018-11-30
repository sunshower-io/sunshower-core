package io.sunshower.model.core.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.sunshower.model.core.AbstractProperty;
import io.sunshower.model.core.PersistenceTest;
import io.sunshower.model.core.Property;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Rollback
@Transactional
class TenantPersistenceTest extends PersistenceTest {
  @PersistenceContext private EntityManager entityManager;

  private Tenant tenant;

  @BeforeEach
  void setUp() {
    tenant = new Tenant();
    tenant.setName("test");
  }

  @Test
  void ensureCascadesWork() {

    val child = new Tenant();
    child.setName("child");

    tenant.addChild(child);
    tenant.setConfigurationValue(AbstractProperty.Type.String, "whatever", "whatevs");
    child.setConfigurationValue(AbstractProperty.Type.String, "cool", "bean");

    entityManager.persist(tenant);
    entityManager.flush();

    String s = entityManager.find(Tenant.class, child.getId()).getConfigurationValue("cool");
    assertThat(s, is("bean"));
  }

  @Test
  void ensureTenantIsPersistable() {
    entityManager.persist(tenant);
    entityManager.flush();
  }

  @Test
  void ensureTenantCanHaveConfigurationsSet() {
    tenant.setConfigurationValue(Property.Type.Secret, "hello", "world");
    entityManager.persist(tenant);
    entityManager.flush();
  }
}
