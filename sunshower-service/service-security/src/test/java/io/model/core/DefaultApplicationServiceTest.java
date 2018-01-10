package io.model.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import io.io.sunshower.service.security.TestSecureService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.Application;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.*;

import java.io.IOException;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional
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
    final User u = new User();
    u.setUsername("Josiah");
    u.setPassword("Haswell");
    u.getDetails().setEmailAddress("josiah@sunshower.io");

    assertFalse(applicationService.isInitialized());

    app.addAdministrator(u);
    applicationService.initialize(app);
    assertTrue(applicationService.isInitialized());
  }

  @Test
  public void ensureInitializedApplicationHasCorrectUsers() {

    Application app = new Application();
    final User u = new User();
    u.setUsername("Josiah");
    u.setPassword("Haswell");
    u.getDetails().setEmailAddress("josiah@sunshower.io");

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
    assertThrows(NoResultException.class, () -> activationService.getActivation());
  }

  @Test
  public void ensureActivatingApplicationProducesRetrievableActivation() {
    final User u = new User();
    u.setUsername("Josiah");
    u.setPassword("Haswell");
    u.getDetails().setEmailAddress("josiah@sunshower.io");
    activationService.activate(u);
    permissionsService.impersonate(
        () -> assertThat(activationService.getActivation().isActive(), is(true)),
        new Role("ADMIN"));
  }

  @Test
  public void ensureReactivatingApplicationFailsWithException() {
    final User u = new User();
    u.setUsername("Josiah");
    u.setPassword("Haswell");
    u.getDetails().setEmailAddress("josiah@sunshower.io");
    activationService.activate(u);

    assertThrows(IllegalStateException.class, () -> activationService.activate(u));
  }
}
