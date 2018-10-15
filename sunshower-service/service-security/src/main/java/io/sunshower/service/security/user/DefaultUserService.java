package io.sunshower.service.security.user;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.UserService;
import io.sunshower.model.core.auth.Details;
import io.sunshower.model.core.auth.User;
import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.service.ext.IconService;
import io.sunshower.service.security.PermissionsService;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultUserService implements UserService, UserDetailsService {

  @Inject private KeyProvider keyProvider;
  @Inject private IconService iconService;
  @Inject private PermissionsService<Permission> permissionsService;

  @PersistenceContext private EntityManager entityManager;

  @Override
  public User save(User u) {
    entityManager.persist(u);
    entityManager.flush();

    permissionsService.impersonate(
        () -> {
          permissionsService.grantWithCurrentSession(
              User.class,
              u,
              BasePermission.READ,
              BasePermission.WRITE,
              BasePermission.DELETE,
              BasePermission.ADMINISTRATION);

          permissionsService.grantWithCurrentSession(Details.class, u.getDetails(), User.class, u);
        },
        u.getUsername());

    return u;
  }

  @Override
  public User get(Identifier id) {
    return entityManager.find(User.class, id);
  }

  @Override
  public User findByUsername(String username) {
    return entityManager
        .createQuery(
            "select u from User u " + "left join fetch u.roles r1 " + "where u.username = :name",
            User.class)
        .setParameter("name", username)
        .getSingleResult();
  }

  @Override
  public User updateDetails(Identifier id, Details details) {
    final User user = entityManager.find(User.class, id);
    if (user == null) {
      throw new EntityNotFoundException("No user with that id");
    }
    user.setDetails(details);
    entityManager.flush();
    return user;
  }

  @Override
  public List<User> activeUsers() {
    return entityManager
        .createQuery(
            "select distinct(u) from User u "
                + "left join fetch u.roles "
                + "left join fetch u.details deets "
                + "where u.active = true",
            User.class)
        .getResultList();
  }

  @Override
  public List<User> inactiveUsers() {
    return entityManager
        .createQuery(
            "select distinct(u) from User u "
                + "left join fetch u.roles "
                + "left join fetch u.details deets "
                + "where u.active = false",
            User.class)
        .getResultList();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    List<User> users =
        entityManager
            .createQuery(
                "select u from User u "
                    + "left join fetch u.roles r1 "
                    + "where u.username = :name",
                User.class)
            .setParameter("name", username)
            .getResultList();

    if (users.size() > 0) {
      return users.get(0);
    }
    throw new UsernameNotFoundException("Failed to locate user: " + username);
  }
}
