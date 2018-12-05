package io.sunshower.service.security.user;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.UserService;
import io.sunshower.model.core.Property;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.persistence.Dialect;
import io.sunshower.service.security.SecurityTest;
import io.sunshower.test.persist.Principal;
import java.util.Arrays;
import java.util.UUID;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

class DefaultUserServiceTest extends SecurityTest {

  @Inject private Dialect dialect;

  @Inject private UserService userService;

  @PersistenceContext private EntityManager entityManager;

  @Principal
  @Transactional
  public User testUser() {
    final User user = new User(Identifier.random(), "josiah22", "coolbeans");
    user.getDetails().setEmailAddress("joe@email.com3242adf");
    user.addRole(new Role("frap"));
    user.setActive(true);
    return user;
  }

  @Test
  @WithMockUser(authorities = "admin")
  public void ensureListingActiveUsersReturnsNoInactiveUsers() {
    final User u = createUser(false);
    entityManager.persist(u);
    assertThat(userService.activeUsers().size(), is(1));
  }

  @Test
  void ensureDetailsServiceRetrievesUserDetails() {
    final User u = createUser(true);
    entityManager.persist(u);
    assertNotNull(userService.getConfiguration(u.getId()));
  }

  @Test
  @WithMockUser(authorities = "admin")
  void ensureSettingConfigurationWorks() {
    val user = createUser(true);
    userService.save(user);
    assertThat(userService.getConfiguration(user.getId()).getProperties().size(), is(1));
    userService.setConfiguration(
        user.getId(),
        Arrays.asList(
            Property.bool("hello", "world", "true"),
            Property.bool("frap", "world", "true"),
            Property.bool("dap", "world", "true")));

    assertThat(userService.getConfiguration(user.getId()).getProperties().size(), is(3));
  }

  @Test
  @WithMockUser(authorities = "admin")
  void ensureDeletingUserWorks() {
    val user = createUser(true);
    userService.save(user);
    userService.delete(user.getId());
    assertThat(userService.get(user.getId()), is(nullValue()));
  }

  @Test
  @WithMockUser(authorities = "admin")
  public void ensureListingInactiveUsersReturnsInactiveUser() {
    final User u = createUser(false);
    entityManager.persist(u);
    assertThat(userService.inactiveUsers().size(), is(1));
  }

  @Test
  @WithMockUser(authorities = "admin")
  public void ensureListingActiveUsersReturnsActiveUsers() {
    final User u = createUser(true);
    entityManager.persist(u);
    assertThat(userService.activeUsers().size(), is(2));
    assertThat(userService.activeUsers().stream().allMatch(User::isActive), is(true));
  }

  @Test
  public void ensureFindByUsernameFails() {
    userService.findByUsername("josiah22");
  }

  @Test
  public void ensureDialectIsInjected() {
    assertThat(dialect, is(not(nullValue())));
  }

  @Test
  public void ensureFindByUsernameFindsUserByExactMatch() {
    final User user = new User(Identifier.random(), "josiah2", "coolbeans");
    user.getDetails().setEmailAddress("joe@email.com3242");
    entityManager.persist(user);

    User saved = userService.findByUsername("josiah2");
    assertThat(saved, is(not(nullValue())));
  }

  private User createUser(boolean b) {
    final User u = new User();
    u.setActive(b);
    u.setUsername(UUID.randomUUID().toString());
    u.setPassword(UUID.randomUUID().toString());
    u.getDetails().setEmailAddress(UUID.randomUUID().toString());
    return u;
  }
}
