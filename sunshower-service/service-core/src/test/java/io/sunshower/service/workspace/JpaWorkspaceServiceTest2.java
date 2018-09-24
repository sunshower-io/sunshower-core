package io.sunshower.service.workspace;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.security.api.SecurityPersistenceConfiguration;
import io.sunshower.service.CoreServiceConfiguration;
import io.sunshower.service.TestConfiguration;
import io.sunshower.service.security.PermissionsService;
import io.sunshower.service.security.SecurityConfiguration;
import io.sunshower.service.workspace.model.Workspace;
import io.sunshower.service.workspace.service.WorkspaceService;
import io.sunshower.test.common.TestConfigurationConfiguration;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("unchecked")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
  classes = {
    SecurityPersistenceConfiguration.class,
    TestConfigurationConfiguration.class,
    HibernateConfiguration.class,
    PersistenceConfiguration.class,
    CoreServiceConfiguration.class,
    DataSourceConfiguration.class,
    FlywayConfiguration.class,
    SecurityConfiguration.class,
    TestConfiguration.class,
  }
)
public class JpaWorkspaceServiceTest2 {

  @Inject private EncryptionService encryptionService;
  @Inject private PermissionsService<?> service;
  @Inject private WorkspaceService workspaceService;

  @PersistenceContext private EntityManager entityManager;

  protected Workspace randomEntity() {
    Workspace workspace = new Workspace();
    workspace.setName(Identifier.random().toString());
    workspace.setKey(Identifier.random().toString());
    return workspace;
  }

  @Test
  public void ensureSavingWorkspaceWorks() {
    //      @Principal
    //      public User createPrincipal(@Authority("admin") Role role, @Authority("tenant:user")
    // Role user) {
    //          final User administrator = new User();
    //          administrator.setUsername("administrator");
    //          administrator.setPassword(encryptionService.encrypt("password"));
    //          administrator.getDetails().setEmailAddress("administrator");
    //          administrator.addRole(role);
    //          administrator.addRole(user);
    //          administrator.setActive(true);
    //          return administrator;
    //      }

    final User user = new User();
    user.setUsername("test");
    user.setPassword(encryptionService.encrypt("whatever"));
    user.getDetails().setEmailAddress("frapper");
    user.addRole(new Role("tenant:user"));
    user.setActive(true);
    entityManager.persist(user);
    setSession(new UsernamePasswordAuthenticationToken("test", "whatever"));
    workspaceService.save(randomEntity());
  }

  protected static Authentication setSession(Authentication a) {
    SecurityContextHolder.getContext().setAuthentication(a);
    return a;
  }
}
