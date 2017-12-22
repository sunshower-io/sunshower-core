package io.sunshower.service.security.user;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.UserService;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.model.core.vault.KeyProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

/**
 * Created by haswell on 10/18/16.
 */
@Transactional
public class DefaultUserService implements UserService, UserDetailsService {

    @Inject
    private KeyProvider keyProvider;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User save(User u) {
        entityManager.persist(u);
        entityManager.flush();
        return u;
    }

    @Override
    public User get(Identifier id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User findByUsername(String username) {
        return entityManager.createQuery("select u from User u " +
                "left join fetch u.roles r1 " +
                "where u.username = :name", User.class)
                .setParameter("name", username).getSingleResult();
    }

    @Override
    public List<User> activeUsers() {
        return entityManager.createQuery(
                "select distinct(u) from User u " +
                        "left join fetch u.roles " +
                        "left join fetch u.details deets " +
                        "where u.active = true", User.class
        ).getResultList();
    }

    @Override
    public List<User> inactiveUsers() {
        return entityManager.createQuery(
                "select distinct(u) from User u " +
                        "left join fetch u.roles " +
                        "left join fetch u.details deets " +
                        "where u.active = false",
                User.class
        ).getResultList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = entityManager
                .createQuery("select u from User u " +
                        "left join fetch u.roles r1 " +
                        "where u.username = :name", User.class)
                .setParameter("name", username)
                .getResultList();

        if (users.size() > 0) {
            return users.get(0);
        }
        throw new UsernameNotFoundException("Failed to locate user: " + username);

    }

}
