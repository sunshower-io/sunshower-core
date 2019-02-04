package io.sunshower.service.security.user;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.UserService;
import io.sunshower.model.core.AbstractProperty;
import io.sunshower.model.core.auth.*;
import io.sunshower.service.security.PermissionsService;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import lombok.val;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultUserService implements UserService, UserDetailsService {

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
  @Cacheable(value = "users", key = "#id")
  public User get(Identifier id) {
    return entityManager.find(User.class, id);
  }

  @Override
  @Cacheable(value = "userdata", key = "#userId")
  public Configuration getConfiguration(Identifier userId) {
    return entityManager.find(User.class, userId).getConfiguration();
  }

  @Override
  @CacheEvict(value = "users", key = "#id")
  public User delete(Identifier id) {
    val user = get(id);
    entityManager.remove(user);
    entityManager.flush();
    return user;
  }

  @Override
  @CacheEvict(value = "userdata", key = "#userId")
  public void setConfiguration(
      Identifier userId, Collection<? extends AbstractProperty> properties) {
    val user = get(userId);
    val cfg = new UserConfiguration();
    for (AbstractProperty prop : properties) {
      cfg.addProperty(prop);
    }
    user.setConfiguration(cfg);
    entityManager.flush();
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
