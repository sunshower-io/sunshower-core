package io.sunshower.service;

import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.DefaultRoles;
import io.sunshower.service.security.SecurityConfiguration;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.persist.AuthenticationTestExecutionListener;
import io.sunshower.test.persist.Authority;
import io.sunshower.test.persist.Principal;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("unchecked")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestExecutionListeners(
  listeners = {
    AuthenticationTestExecutionListener.class,
    WithSecurityContextTestExecutionListener.class
  },
  mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@ContextConfiguration(classes = SecurityConfiguration.class)
public abstract class AuthenticatedTestCase extends ServiceTestCase {

  @Inject private RoleService roleService;

  @PersistenceContext protected EntityManager entityManager;

  @Inject private EncryptionService encryptionService;

  public AuthenticatedTestCase() {
    this(SerializationAware.Format.JSON, new Class[0]);
  }

  public AuthenticatedTestCase(SerializationAware.Format format, Class[] bound) {
    super(format, bound);
  }

  protected static void changeSession(String userId, String unencryptedPassword) {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(userId, unencryptedPassword));
  }

  protected static Authentication clearSession() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    SecurityContextHolder.getContext().setAuthentication(null);
    return authentication;
  }

  @Authority("admin")
  public Role adminRole() {
    return new Role("admin");
  }

  @Authority("tenant:user")
  public Role tenantUserRole() {
    return DefaultRoles.TENANT_USER.toRole();
  }

  @Principal
  public User createPrincipal(@Authority("admin") Role role, @Authority("tenant:user") Role user) {
    final User administrator = new User();
    administrator.setUsername("administrator");
    administrator.setPassword(encryptionService.encrypt("password"));
    administrator.getDetails().setEmailAddress("administrator");
    administrator.addRole(role);
    administrator.addRole(user);
    administrator.setActive(true);
    return administrator;
  }
}
