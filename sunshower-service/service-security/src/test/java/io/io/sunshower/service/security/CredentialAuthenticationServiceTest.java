package io.io.sunshower.service.security;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.CredentialService;
import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.SecurityTest;
import io.sunshower.service.security.TokenAuthenticationFilter;
import io.sunshower.service.security.crypto.MessageAuthenticationCode;
import java.io.IOException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

public class CredentialAuthenticationServiceTest extends SecurityTest {

  @Inject private CredentialService credentialService;

  @Inject private TestSecureService testSecureService;

  @Inject private TokenAuthenticationFilter tokenAuthenticationFilter;

  @Inject private MessageAuthenticationCode messageAuthenticationCode;

  @Inject private EncryptionService encryptionService;

  @PersistenceContext private EntityManager entityManager;

  @Inject private RoleService roleService;

  @Inject private UserDetails user;

  @Inject private Authentication authentication;

  @Test
  @WithMockUser(
    username = "admin",
    authorities = {"ROLE_ADMIN"}
  )
  public void ensureAuthenticationIsInjected() {
    assertThat(authentication, is(not(nullValue())));
  }

  @Test
  @WithMockUser(
    username = "admin",
    authorities = {"ROLE_ADMIN"}
  )
  public void ensureUserIsInjected() {
    assertThat(user, is(not(nullValue())));
    assertThat(user.getUsername(), is("admin"));
  }

  @Test
  @Transactional
  public void ensureSayHelloAdminWorks() throws IOException {
    final User user = new User();
    user.setPassword("frapasdfasdf");
    user.setUsername("joe@email.com3242");
    user.getDetails().setEmailAddress("joe@email.com3242");
    final Role adminRole = roleService.findOrCreate(new Role("admin", "coolbeans"));
    user.addRole(adminRole);
    entityManager.persist(user);
    Identifier id = user.getId();
    String token = encryptionService.createToken(user);

    final ContainerRequestContext context = Mockito.mock(ContainerRequestContext.class);
    given(context.getHeaderString(TokenAuthenticationFilter.HEADER_KEY)).willReturn(token);
    tokenAuthenticationFilter.filter(context);
    testSecureService.sayHelloAdmin();
  }

  @Test
  @Transactional
  public void ensureSayHelloAdminWorksOnRolesAllowedWorks() throws IOException {
    final User user = new User();
    user.setUsername("joe@email.com3242");
    user.setPassword("frapasdfasdfasdf");
    user.getDetails().setEmailAddress("joe@email.com3242");
    final Role adminRole = roleService.findOrCreate(new Role("ROLE_ADMIN", "coolbeans"));
    user.addRole(adminRole);
    entityManager.persist(user);
    Identifier id = user.getId();
    String token = encryptionService.createToken(user);

    final ContainerRequestContext context = Mockito.mock(ContainerRequestContext.class);
    given(context.getHeaderString(TokenAuthenticationFilter.HEADER_KEY)).willReturn(token);
    tokenAuthenticationFilter.filter(context);
    testSecureService.sayHelloRolesAllowed();
  }

  @Test
  @WithMockUser(
    username = "admin",
    roles = {
      "user", "admin",
    }
  )
  public void ensureMethodIsProtected() {
    testSecureService.sayHelloAdmin();
  }

  @Test
  @WithMockUser(
    username = "admin",
    authorities = {"ROLE_ADMIN"}
  )
  public void ensureMethodIsProtectedByRolesAllowed() {
    testSecureService.sayHelloRolesAllowed();
  }

  @Test
  @WithMockUser(
    username = "admin",
    roles = {"frap"}
  )
  public void ensureMethodIsProtectedByRolesAllowed_denied() {
    try {
      testSecureService.sayHelloRolesAllowed();
      fail("Expected authorization rejection");
    } catch (AccessDeniedException ex) {

    }
  }

  @Test
  @WithMockUser(
    username = "admin",
    roles = {"notadmin"}
  )
  public void ensureMethodIsDenied() {
    try {
      testSecureService.sayHelloAdmin();
      fail("Was supposed to get an access denied exception");
    } catch (AccessDeniedException ex) {

    }
  }

  @Test
  public void ensureServiceIsInjected() {
    assertThat(credentialService, is(not(nullValue())));
  }
}
