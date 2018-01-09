package io.sunshower.service;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.persistence.core.Persistable;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.persist.Authority;
import io.sunshower.test.persist.Principal;
import java.io.Serializable;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;

/** Created by haswell on 5/16/17. */
public abstract class BaseRepositoryTest<ID extends Serializable, E extends Persistable<ID>>
    extends AuthenticatedTestCase {

  @Inject private RoleService roleService;

  @Inject private EncryptionService encryptionService;

  public BaseRepositoryTest() {
    super();
  }

  public BaseRepositoryTest(SerializationAware.Format format, Class[] bound) {
    super(format, bound);
  }

  @Principal
  public User createNoRoles() {
    final User administrator = new User();
    administrator.setUsername("no-roles");
    administrator.setActive(true);
    administrator.setPassword(encryptionService.encrypt("frapafadsfa"));
    administrator.getDetails().setEmailAddress("no@workspace-roles.com");
    return administrator;
  }

  @Principal
  public User createNonAdminUser(@Authority("tenant:user") Role role) {
    final User administrator = new User();
    administrator.setUsername("non-admin");
    administrator.setActive(true);
    administrator.setPassword(encryptionService.encrypt("frapafadsfa"));
    administrator.getDetails().setEmailAddress("some@workspace-roles.com");
    administrator.addRole(role);
    return administrator;
  }

  protected abstract ID randomId();

  protected abstract E randomEntity();

  protected abstract void alter(E random);

  protected abstract BaseRepository<ID, E> service();

  protected abstract void expectAlteration(ID id, E random);

  protected abstract void expectSameProperties(E random, E save);

  @Test
  @WithUserDetails("non-admin")
  public void ensureNonAdminCanCreateAndRetrieveEntityViaGet() {
    final E random = randomEntity();
    service().save(random);
    service().get(random.getId());
  }

  @Test
  @WithUserDetails("non-admin")
  public void ensureNonAdminCanDeleteEntityTransactionally() {
    final E random = randomEntity();
    service().create(random);
    service().delete(random.getId());
    try {
      service().get(random.getId());
      fail("Should not have been able to retrieve deleted entity");
    } catch (AccessDeniedException ex) {

    }
  }

  @Test
  @WithUserDetails("non-admin")
  public void ensureNonAdminCanDeleteEntity() {
    final E random = randomEntity();
    service().create(random);
    service().delete(random.getId());
  }

  @Test
  @WithUserDetails("no-roles")
  public void ensureUserWithoutCreateEntityRoleCannotCreateEntity() {
    assertThrows(
        AccessDeniedException.class,
        () -> {
          final E random = randomEntity();
          service().create(random);
        });
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureAdministratorCanCreateEntityRole() {
    final E random = randomEntity();
    service().create(random);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureNonOwnerCannotUpdateEntity() {
    final E random = randomEntity();
    service().create(random);
    changeSession("no-roles", "frapafadsfa");
    try {
      service().update(random);
      fail("Should not have been able to update that workspace");
    } catch (AccessDeniedException ex) {

    }
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureOwnerCanUpdateEntity() {
    E random = randomEntity();
    random = service().create(random);
    service().update(random);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureOwnerCanViewCreatedEntity() {
    final E random = randomEntity();
    service().create(random);
    assertThat(service().list().size(), is(1));
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureNonOwnerCannotViewEntity() {
    final E random = randomEntity();
    service().create(random);
    changeSession("non-admin", "frapafadsfa");
    assertThat(service().list().size(), is(0));
    changeSession("administrator", "password");
    assertThat(service().list().size(), is(1));
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureUserCanSaveManyEntities() {
    for (int i = 0; i < 10; i++) {
      E e = randomEntity();
      service().create(e);
    }
    assertThat(service().list().size(), is(10));
    changeSession("non-admin", "frapafadsfa");
    assertThat(service().list().size(), is(0));
    changeSession("administrator", "password");
    assertThat(service().list().size(), is(10));
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureSaveIsIdempotent() {
    E random = randomEntity();
    random = service().save(random);
    ID id = random.getId();
    assertThat(service().list().size(), is(1));
    random = service().save(random);
    ID id2 = random.getId();
    assertThat(service().list().size(), is(1));
    assertThat(id, is(id2));
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureSaveUpdatesName() {
    E random = randomEntity();
    service().save(random);
    E save = service().get(random.getId());
    expectSameProperties(random, save);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureGetFailsWhenUserDoesNotHaveAccessToEntity() {
    final E random = randomEntity();
    service().save(random);
    changeSession("no-roles", "frapafadsfa");
    try {
      service().get(random.getId());
      fail("Should not have been able to access workspace");
    } catch (AccessDeniedException ex) {

    }
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureUpdatingEntityCanChangeIt() {
    E random = randomEntity();
    service().save(random);
    service().get(random.getId());
    alter(random);
    expectAlteration(random.getId(), random);
    assertThat(service().list().size(), is(1));
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureUserCannotUpdateEntityIfUserDoesNotHaveWriteAccess() {
    E random = randomEntity();
    service().save(random);
    changeSession("no-roles", "frapafadsfa");
    alter(random);
    try {
      service().save(random);
      fail("Should not have gotten here");
    } catch (AccessDeniedException ex) {

    }
  }

  @WithUserDetails("administrator")
  public void ensureDeletingNonExistantEntityThrowsNoEntityException() {
    assertThrows(
        EntityNotFoundException.class,
        () -> {
          service().delete(randomId());
        });
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureDeletingExistingEntityWithAccessWorks() {
    E random = randomEntity();
    service().save(random);
    assertThat(service().delete(random.getId()), is(random));
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureUserWithoutDeleteAccessCannotDeleteEntity() {
    final E random = randomEntity();
    service().save(random);
    changeSession("no-roles", "frapafadsfa");
    try {
      assertThat(service().delete(random.getId()), is(random));
      fail("Should not have been able to delete");
    } catch (AccessDeniedException ex) {

    }
  }
}
