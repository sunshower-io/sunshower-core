package io.sunshower.model.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.Tenant;
import io.sunshower.model.core.auth.TenantDetails;
import io.sunshower.model.core.auth.User;
import io.sunshower.model.core.io.File;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

class UserPersistenceTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureEntityManagerIsInjected() {
    assertThat(entityManager, is(not(nullValue())));
  }

  @Test
  public void ensureSavingPersonWorks() {
    final User user = new User();
    user.getDetails().setEmailAddress("joe@whatever.com");
    user.setUsername("frap");
    user.setPassword("asdfasdfasdfasfasdfasf");
    entityManager.persist(user);
    entityManager.flush();
  }

  @Test
  public void ensurePersonIsSavedWithActiveFalse() {

    final User user = new User();
    user.getDetails().setEmailAddress("joe@whatever.com");
    user.setUsername("frap");
    user.setPassword("asdfasdfasdfasf");
    entityManager.persist(user);
    entityManager.flush();

    final User saved = entityManager.find(User.class, user.getId());
    assertFalse(saved.isEnabled());
  }

  @Test
  public void ensureUserWithRoleCanBePersisted() {
    final User user = new User();
    user.setUsername("whatever");
    user.setPassword("whatever");
    user.getDetails().setFirstname("whatever");
    user.getDetails().setEmailAddress("frap@dap.wab");

    final Role role = new Role("coolbeans");
    user.addRole(role);
    entityManager.persist(user);
    entityManager.flush();
  }

  @Test
  public void ensureRoleCanBeRemoved() {
    final User user = new User();
    final Role role = new Role("coolbeans");
    user.addRole(role);
    assertThat(user.getRoles().size(), is(1));
    assertThat(user.getRoles().stream().allMatch(t -> t.getUsers().size() == 1), is(true));
    user.removeRole(role);
    assertThat(user.getRoles().size(), is(0));
    assertThat(role.getUsers().size(), is(0));
  }

  @Test
  public void ensureSavingUserDetailsFileWorks() {
    final User user = new User();
    user.getDetails().setEmailAddress("joe@whatever.com2");
    user.setUsername("fraafp");
    user.setPassword("asdfasdfasdfasfadsfadf");
    user.getDetails().setRoot(new File("coolbeans"));
  }

  @Test
  public void ensureRegisteredIsPersisted() {

    final User user = new User();
    user.getDetails().setEmailAddress("joe@whatever.com");
    user.setUsername("frap");
    user.setPassword("asdfasdfasdfasf");
    final Date date = new Date();
    user.getDetails().setRegistered(date);
    entityManager.persist(user);
    entityManager.flush();

    final User saved = entityManager.find(User.class, user.getId());

    assertThat(saved.getDetails().getRegistered(), is(date));
  }

  @Test
  public void ensureTenantCascadesSaveFile() {
    final Tenant tenant = new Tenant();
    tenant.setName("coke2");
    tenant.setDetails(new TenantDetails());
    final User user = new User();
    user.getDetails().setEmailAddress("joe@3whatever.com");
    user.setUsername("frapadfasdf");
    user.setPassword("asdfasdfasdfasfadfa");
    tenant.addUser(user);

    final Tenant cokehr = new Tenant();
    cokehr.setName("cokeh2r");

    cokehr.setDetails(new TenantDetails());
    tenant.addChild(cokehr);
    tenant.getDetails().setRoot(new File("cool"));

    entityManager.persist(tenant);
    entityManager.flush();

    assertThat(
        entityManager.find(Tenant.class, tenant.getId()).getDetails().getRoot(),
        is(not(nullValue())));

    assertThat(
        entityManager.find(Tenant.class, tenant.getId()).getDetails().getRoot().getPath(),
        is("cool"));
    assertThat(entityManager.find(Tenant.class, tenant.getId()).getUsers().size(), is(1));
    assertThat(entityManager.find(Tenant.class, tenant.getId()).getChildren().size(), is(1));
  }

  @Test
  public void ensureTenantCascadesSaveToComplexUser() {
    final Tenant tenant = new Tenant();
    tenant.setName("coke");
    tenant.setDetails(new TenantDetails());
    final User user = new User();
    user.getDetails().setEmailAddress("joe@2whatever.com");
    user.setUsername("frapadfasdf");
    user.setPassword("asdfasdfasdfasfadfa");
    tenant.addUser(user);

    final Tenant cokehr = new Tenant();
    cokehr.setName("cokehr");

    cokehr.setDetails(new TenantDetails());
    tenant.addChild(cokehr);

    entityManager.persist(tenant);
    entityManager.flush();

    assertThat(entityManager.find(Tenant.class, tenant.getId()).getUsers().size(), is(1));
    assertThat(entityManager.find(Tenant.class, tenant.getId()).getChildren().size(), is(1));
  }
}
