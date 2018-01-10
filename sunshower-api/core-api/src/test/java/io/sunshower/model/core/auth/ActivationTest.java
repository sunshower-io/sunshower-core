package io.sunshower.model.core.auth;

import io.sunshower.model.core.Application;
import io.sunshower.model.core.PersistenceTest;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

public class ActivationTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureActivationCanBeSaved() {
    final Activation activation = new Activation();
    final User user = new User();
    user.setPassword("test12234");
    user.setUsername("test");
    user.setActive(true);
    user.getDetails().setEmailAddress("josaiah@gmail.com");
    activation.setActivator(user);

    Application application = new Application();
    activation.setApplication(application);
    entityManager.persist(activation);
    entityManager.flush();
  }
}
