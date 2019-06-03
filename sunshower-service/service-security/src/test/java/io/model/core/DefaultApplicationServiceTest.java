package io.model.core;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import io.io.sunshower.service.security.TestSecureService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.Application;
import io.sunshower.model.core.auth.Activation;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.*;
import java.io.IOException;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;

class DefaultApplicationServiceTest extends SecurityTest {

  @PersistenceContext private EntityManager entityManager;

  @Inject private PermissionsService<?> permissionsService;

  @Inject private ActivationService activationService;

  @Inject private TestSecureService testSecureService;

  @Inject private ApplicationService applicationService;

  @Inject private EncryptionService encryptionService;

  @Inject private TokenAuthenticationFilter tokenAuthenticationFilter;

  @Test
  public void ensureApplicationCanBePersistedCorrectly() {
    final Application application = new Application();
    entityManager.persist(application);
    entityManager.flush();
  }

  @Test
  public void ensureDeletingActivatorRemovesActivator() {

    final User u = createTestUser();
    activationService.activate(u);
    permissionsService.impersonate(
        () -> {
          assertThat(activationService.getActivation().isActive(), is(true));
          Integer count = activationService.list().size();
          assertThat(count, is(1));
          Activation activation = activationService.list().get(0);
          activationService.delete(activation);
          count = activationService.list().size();
          assertThat(count, is(0));
        },
        new Role("admin"));
  }

  @NotNull
  private User createTestUser() {
    final User u = new User();
    u.setUsername("Josiah");
    u.setPassword("Haswell");
    u.getDetails().setEmailAddress("josiah@sunshower.io");
    return u;
  }

  @Test
  public void ensureSayHelloAdminWorks() throws IOException {
    final User user = new User();
    user.setPassword("frapasdfasdf");
    user.setUsername("joe@email.com3242");
    user.getDetails().setEmailAddress("joe@email.com3242");

    applicationService.addAdministrator(user);
    String token = encryptionService.createToken(user);

    final ContainerRequestContext context = Mockito.mock(ContainerRequestContext.class);
    given(context.getHeaderString(TokenAuthenticationFilter.HEADER_KEY)).willReturn(token);
    tokenAuthenticationFilter.filter(context);
    testSecureService.sayHelloTenantUser();
  }

  @Test
  public void ensureApplicationCanBeInitializedCorrectly() {
    Application app = new Application();
    final User u = createTestUser();

    assertFalse(applicationService.isInitialized());

    app.addAdministrator(u);
    applicationService.initialize(app);
    assertTrue(applicationService.isInitialized());
  }

  @Test
  public void ensureInitializedApplicationHasCorrectUsers() {

    Application app = new Application();
    final User u = createTestUser();

    app.addAdministrator(u);
    applicationService.initialize(app);
    assertThat(applicationService.getAdministrators().size(), is(1));

    Set<User> admins = applicationService.getAdministrators();
    User admin = admins.iterator().next();
    assertThat(admin.getAuthorities().size(), is(1));
  }

  @Test
  public void ensureActivationServiceIsInjectable() {
    assertThat(activationService, is(not(nullValue())));
  }

  @Test
  public void ensureNoActivationsAreInitiallyAvailable() {
    permissionsService.impersonate(
        () -> {
          assertThrows(NoResultException.class, () -> activationService.getActivation());
        },
        new Role("admin"));
  }

  @Test
  public void ensureActivationServiceIsActiveRequiresNoAuthentication() {
    activationService.isActive();
  }

  @Test
  public void ensureActivatingApplicationProducesRetrievableActivation() {
    final User u = createTestUser();
    activationService.activate(u);
    permissionsService.impersonate(
        () -> assertThat(activationService.getActivation().isActive(), is(true)),
        new Role("admin"));
  }

  @Test
  public void ensureDeactivationCannotBeCalledByANonAdmin() {
    final User u = createTestUser();
    activationService.activate(u);
    assertThat(activationService.isActive(), is(true));
    assertThrows(
        AccessDeniedException.class,
        () ->
            permissionsService.impersonate(
                () -> activationService.deactivate(), new Role("frapper")));
  }

  public void ensureDeactivationWorks() {
    final User u = createTestUser();
    activationService.activate(u);
    assertThat(activationService.isActive(), is(true));
    permissionsService.impersonate(
        () -> {
          activationService.deactivate();
        },
        new Role("admin"));
    assertThat(activationService.isActive(), is(false));
  }

  @Test
  public void ensureReactivatingApplicationFailsWithException() {
    final User u = createTestUser();
    activationService.activate(u);

    assertThrows(IllegalStateException.class, () -> activationService.activate(u));
  }
}
