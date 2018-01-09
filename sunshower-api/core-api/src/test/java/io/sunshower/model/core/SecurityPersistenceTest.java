package io.sunshower.model.core;

import io.sunshower.model.core.auth.Permission;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

public class SecurityPersistenceTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureSavingAUserWithNoRolesWorks() {
    User user = new User();
    user.setUsername("josiah");
    user.setPassword("testasdfasdf");
    user.getDetails().setEmailAddress("josiah@whatever");
    entityManager.persist(user);
    entityManager.flush();
  }

  @Test
  public void ensureSavingAUserWithARoleWorks() {
    User user = new User();
    user.setUsername("Josiah");
    user.setPassword("Haswell");
    user.addRole(new Role("admin"));
    user.getDetails().setEmailAddress("josiah@whatever");
    entityManager.persist(user);
    entityManager.flush();
  }

  @Test
  public void ensureSavingAUserWithARoleWithAPermissionWorks() {

    User user = new User();
    user.setUsername("Josiah");
    user.setPassword("Haswell");
    user.addRole(new Role("admin").addPermission(new Permission("cool")));
    user.getDetails().setEmailAddress("josiah@whatever");
    entityManager.persist(user);
    entityManager.flush();
  }
}
