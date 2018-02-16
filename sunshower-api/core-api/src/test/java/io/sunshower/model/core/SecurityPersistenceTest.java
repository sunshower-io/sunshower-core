package io.sunshower.model.core;

import io.sunshower.model.core.auth.Permission;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

public class SecurityPersistenceTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureSavingAUserWithNoRolesWorks() {
    User user = new User();
    user.setUsername("josiah");
    user.setPassword("testasdfasdf");
    user.getDetails().setEmailAddress("josiah2@whatever");
    entityManager.persist(user);
    entityManager.flush();
  }

  @Test
  @Rollback
  public void ensureSavingAUserWithARoleWorks() {
    User user = new User();
    user.setUsername("1Josiah");
    user.setPassword("Haswell");
    user.addRole(new Role("admin"));
    user.getDetails().setEmailAddress("josiah223@whatever");
    entityManager.persist(user);
    entityManager.flush();
  }

  @Test
  @Rollback
  public void ensureSavingAUserWithARoleWithAPermissionWorks() {

    User user = new User();
    user.setUsername("Josiah");
    user.setPassword("Haswell");
    user.addRole(new Role("admin1").addPermission(new Permission("cool")));
    user.getDetails().setEmailAddress("josiah5@whatever");
    entityManager.persist(user);
    entityManager.flush();
  }
}
