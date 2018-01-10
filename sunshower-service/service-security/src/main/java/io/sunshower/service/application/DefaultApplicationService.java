package io.sunshower.service.application;

import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.Application;
import io.sunshower.model.core.ApplicationInitializationException;
import io.sunshower.model.core.auth.Activation;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.ActivationService;
import io.sunshower.service.security.ApplicationService;
import io.sunshower.service.security.DefaultRoles;
import io.sunshower.service.signup.SignupService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefaultApplicationService implements ApplicationService, ActivationService {

  @Inject private RoleService roleService;

  @PersistenceContext private EntityManager entityManager;

  @Inject private SignupService signupService;

  @Inject private EncryptionService encryptionService;

  @Override
  public Application instance() {
    return entityManager
        .createQuery("select a from Application a", Application.class)
        .getSingleResult();
  }

  @Override
  public Boolean isInitialized() {
    return entityManager
            .createQuery(
                "select count(a) from Application a " + "where a.enabled = true", Long.class)
            .getSingleResult()
        == 1;
  }

  @Override
  public Application initialize(Application application) {
    if (!isInitialized()) {
      Collection<User> administrators = application.getAdministrators();
      if (administrators == null || administrators.size() == 0) {
        throw new ApplicationInitializationException(
            "You must add at least one administrator "
                + "or the application will not be accessible");
      }

      for (User u : administrators) {
        addAdministrator(u);
      }
      application.setEnabled(true);
      entityManager.persist(application);
      entityManager.flush();
      return application;
    } else {
      return instance();
    }
  }

  @Override
  public Set<User> getAdministrators() {
    return new HashSet<>(
        entityManager
            .createQuery(
                "select u from User u "
                    + "left join fetch u.roles as r"
                    + " where r.authority = 'admin'",
                User.class)
            .getResultList());
  }

  @Override
  public Boolean addAdministrator(User user) {
    final Role userRole = roleService.findOrCreate(DefaultRoles.SITE_ADMINISTRATOR.toRole());
    user.addRole(userRole);
    user.setPassword(encryptionService.encrypt(user.getPassword()));
    entityManager.merge(user);
    entityManager.flush();
    return true;
  }

  @Override
  public Boolean removeAdministrator(User user) {
    return null;
  }

  @Override
  @PreAuthorize("hasAuthority('ADMIN')")
  public Activation getActivation() {
    return entityManager
        .createQuery("select a from Activation a where a.active = true", Activation.class)
        .getSingleResult();
  }

  @Override
  public boolean isActive() {
    return entityManager
            .createQuery("select count(a) from Activation a where a.active = true", Long.class)
            .getSingleResult()
        > 0;
  }

  public Activation activate(User activator) {
    checkActive();
    final Activation activation = new Activation();
    activation.setActive(true);
    final Application application = new Application();
    application.setName("Sunshower");
    application.setAdministrators(Arrays.asList(activator));
    activation.setActivator(activator);
    activation.setApplication(application);
    entityManager.persist(activation);
    return activation;
  }

  @PreAuthorize("!activationService.isActive()")
  public Activation deactivate() {
    return null;
  }

  private void checkActive() {
    if (isActive()) {
      throw new IllegalStateException(
          String.format("%s is already active!", getActivation().getApplication().getName()));
    }
  }
}
