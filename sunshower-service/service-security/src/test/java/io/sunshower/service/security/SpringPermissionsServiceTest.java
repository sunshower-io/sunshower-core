package io.sunshower.service.security;

import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.acls.model.Permission;

class SpringPermissionsServiceTest extends SecurityTest {

  @PersistenceContext private EntityManager entityManager;
  @Inject private PermissionsService<Permission> permissionsService;

  User randomUser() {

    final User user = new User(User.sequence.next(), "user", "coolbeans");
    user.getDetails().setEmailAddress("joe@email.com3242adf");
    user.addRole(new Role("frap"));
    user.setActive(true);
    return user;
  }

  @Test
  void ensureImpersonatingByIdWorks() {
    User user = randomUser();
    entityManager.persist(user);
    permissionsService.impersonate(() -> {}, user.getId());
  }

  @Test
  void ensureImpersonatingByUsernameWorks() {
    User user = randomUser();
    entityManager.persist(user);
    permissionsService.impersonate(() -> {}, user.getUsername());
  }
}
