package io.sunshower.service.security;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.signup.RegistrationRequest;
import io.sunshower.service.signup.SignupService;
import java.util.*;
import javax.inject.Inject;
import javax.persistence.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

@Service
@Transactional
public class DefaultSignupService implements SignupService {

  @Inject private RoleService roleService;

  @PersistenceContext private EntityManager entityManager;

  @Inject private EncryptionService encryptionService;

  @Inject private ApplicationContext applicationContext;

  @Override
  public RegistrationRequest signup(User user) {
    if (user.getRoles() != null) {
      user.getRoles().clear();
    }
    user.setActive(false);
    final RegistrationRequest registrationRequest = new RegistrationRequest(user);
    entityManager.persist(registrationRequest);

    final Role role = roleService.findOrCreate(DefaultRoles.TENANT_USER.toRole());

    user.setPassword(encryptionService.encrypt(user.getPassword()));
    user.addRole(role);

    entityManager.merge(user);
    entityManager.flush();

    return registrationRequest;
  }

  private void executePostSignup(User user) {
    Map<String, Object> beansWithAnnotation =
        applicationContext.getBeansWithAnnotation(PostSignupListener.class);
    for (Map.Entry<String, Object> bean : beansWithAnnotation.entrySet()) {
      performPostSignup(bean, user);
    }
  }

  private void performPostSignup(Map.Entry<String, Object> bean, User user) {
    Class<?> type = AopUtils.getTargetClass(bean.getValue());
    ReflectionUtils.doWithMethods(
        type,
        m -> {
          ReflectionUtils.invokeMethod(m, bean.getValue(), user);
        },
        t -> {
          if (t.isAnnotationPresent(PostSignup.class)) {
            if (t.getParameterCount() == 1 && t.getParameterTypes()[0].equals(User.class)) {
              return true;
            } else {
              throw new IllegalStateException(
                  "PostSignup method requires " + "exactly 1 parameter of type user");
            }
          }
          return false;
        });
  }

  @Override
  @PreAuthorize("hasAuthority('admin')")
  public List<User> list() {
    return entityManager
        .createQuery("select u from User u left join fetch u.roles ", User.class)
        .getResultList();
  }

  @Override
  @PreAuthorize("hasAuthority('admin')")
  public User revoke(Identifier uuid) {
    User user = entityManager.find(User.class, uuid);
    if (user != null) {
      user.setActive(false);
      signup(user);
      return user;
    } else {
      throw new EntityNotFoundException("No user identified by '" + uuid + "' was found!");
    }
  }

  @Override
  @PreAuthorize("hasAuthority('admin')")
  public User approve(String s) {
    final List<RegistrationRequest> u =
        entityManager
            .createQuery(
                "select r from "
                    + "RegistrationRequest r "
                    + "inner join r.user u "
                    + "inner join u.roles "
                    + "inner join u.details "
                    + "where r.requestId = :id",
                RegistrationRequest.class)
            .setParameter("id", s)
            .getResultList();
    if (u.size() == 1) {
      final RegistrationRequest request = u.get(0);
      final User user = request.getUser();
      user.setActive(true);
      entityManager.remove(request);
      entityManager.flush();
      executePostSignup(user);
      return user;
    }
    throw new EntityNotFoundException("No registration request exists");
  }

  @Override
  @PreAuthorize("hasAuthority('admin')")
  public List<RegistrationRequest> pendingRegistrations() {
    return entityManager
        .createQuery(
            "select r from RegistrationRequest r "
                + "inner join r.user as u "
                + "inner join u.roles as roles",
            RegistrationRequest.class)
        .getResultList();
  }
}
