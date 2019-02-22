package io.sunshower.service.security;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.Details;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.signup.RegistrationRequest;
import io.sunshower.service.signup.SignupService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

@Service
@Transactional
public class DefaultSignupService implements SignupService {

  @Inject private RoleService roleService;

  @Inject private EncryptionService encryptionService;

  @PersistenceContext private EntityManager entityManager;
  @Inject private ApplicationContext applicationContext;
  @Inject private PermissionsService<Permission> permissionsService;

  @Override
  public RegistrationRequest signup(User user) {
    return signup(user, Collections.emptyList());
  }

  @Override
  public RegistrationRequest signup(User user, List<String> productIds) {
    if (user.getRoles() != null) {
      user.getRoles().clear();
    }
    user.setActive(true);
    final RegistrationRequest registrationRequest = new RegistrationRequest(user);
    entityManager.persist(registrationRequest);

    final Role role = roleService.findOrCreate(DefaultRoles.TENANT_USER.toRole());

    user.setPassword(encryptionService.encrypt(user.getPassword()));
    user.addRole(role);

    entityManager.merge(user);
    entityManager.flush();
    permissionsService.impersonate(
        () -> {
          permissionsService.grantWithCurrentSession(
              User.class,
              user,
              BasePermission.READ,
              BasePermission.WRITE,
              BasePermission.DELETE,
              BasePermission.ADMINISTRATION);
          permissionsService.grantWithCurrentSession(
              Details.class,
              user.getDetails(),
              BasePermission.READ,
              BasePermission.WRITE,
              BasePermission.DELETE,
              BasePermission.ADMINISTRATION);
        },
        user.getUsername());

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
      signup(user);
      user.setActive(false);
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
                + "inner join u.roles as roles "
                + "left outer join u.details as details "
                + "left outer join details.image as image",
            RegistrationRequest.class)
        .getResultList();
  }
}
