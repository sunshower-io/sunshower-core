package io.sunshower.model.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import io.sunshower.io.Files;
import io.sunshower.model.core.auth.*;
import io.sunshower.model.core.io.File;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

@Rollback
class UserPersistenceTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureEntityManagerIsInjected() {
    assertThat(entityManager, is(not(nullValue())));
  }

  @Test
  public void ensureSavingPersonWorks() {
    final User user = new User();
    user.getDetails().setEmailAddress("joe@134whatever.com");
    user.setUsername("frapasdfasdf");
    user.setPassword("asdfasdfasdfasfasdfasf");
    entityManager.persist(user);
    entityManager.flush();
  }

  @Test
  @SneakyThrows
  void ensureImageCanBeSavedOnUser() {

    final User user = new User();
    user.getDetails().setEmailAddress("joe@134whatever.com");
    user.setUsername("coolbeans");
    user.setPassword("whatever");

    Image icon = new Image();
    icon.setData(Files.read(ClassLoader.getSystemResourceAsStream("icons/kubernetes.png")));
    user.getDetails().setImage(icon);

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
  void ensureUserWithRoleCanBePersisted() {
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
  void ensureRoleCanBeRemoved() {
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
  void ensureSavingUserDetailsFileWorks() {
    final User user = new User();
    user.getDetails().setEmailAddress("joe@whatever.com2");
    user.setUsername("fraafp");
    user.setPassword("asdfasdfasdfasfadsfadf");
    user.getDetails().setRoot(new File("coolbeans"));
  }

  @Test
  void ensureRegisteredIsPersisted() {

    final User user = new User();
    user.getDetails().setEmailAddress("joe2@whatever.com");
    user.setUsername("frap1212");
    user.setPassword("asdfasdfasdfasf");
    final Date date = new Date();
    user.getDetails().setRegistered(date);
    entityManager.persist(user);
    entityManager.flush();

    final User saved = entityManager.find(User.class, user.getId());

    assertThat(saved.getDetails().getRegistered(), is(date));
  }

  @Test
  @SneakyThrows
  void ensureTenantCascadesSaveFile() {
    final Tenant tenant = new Tenant();
    tenant.setName("coke2");
    tenant.setDetails(new TenantDetails());
    Image icon = new Image();
    icon.setData(Files.read(ClassLoader.getSystemResourceAsStream("icons/kubernetes.png")));
    tenant.getDetails().setImage(icon);

    final User user = new User();
    user.getDetails().setEmailAddress("joe@3whatever.com");
    user.setUsername("frapadfasdf13213");
    user.setPassword("asdfasdfasdfasfadfa");
    tenant.addUser(user);
    user.getDetails().setImage(icon);

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
    final User user = new User();
    user.getDetails().setEmailAddress("joe@2whatever.com");
    user.setUsername("frapadfasdf");
    user.setPassword("asdfasdfasdfasfadfa");
    tenant.addUser(user);

    final Tenant cokehr = new Tenant();
    cokehr.setName("cokehr");

    tenant.addChild(cokehr);

    entityManager.persist(tenant);
    entityManager.flush();

    assertThat(entityManager.find(Tenant.class, tenant.getId()).getUsers().size(), is(1));
    assertThat(entityManager.find(Tenant.class, tenant.getId()).getChildren().size(), is(1));
  }
}
