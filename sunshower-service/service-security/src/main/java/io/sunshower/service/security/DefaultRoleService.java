package io.sunshower.service.security;

import io.sunshower.core.security.RoleService;
import io.sunshower.model.core.auth.Role;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Created by haswell on 10/26/16. */
@Service
@Transactional
public class DefaultRoleService implements RoleService {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Role findOrCreate(Role role) {

    List<Role> authorities =
        entityManager
            .createQuery("select r from Role r " + "where r.authority = :authority", Role.class)
            .setParameter("authority", role.getAuthority())
            .getResultList();
    if (authorities.size() == 1) {
      return authorities.get(0);
    } else {
      entityManager.persist(role);
      return role;
    }
  }
}
