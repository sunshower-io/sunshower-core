package io.sunshower.service.application;
import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.Application;
import io.sunshower.model.core.ApplicationInitializationException;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.DefaultRoles;
import io.sunshower.service.signup.SignupService;
import io.sunshower.service.security.ApplicationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Created by haswell on 10/26/16.
 */
@Service
@Transactional
public class DefaultApplicationService implements ApplicationService {


    @Inject
    private RoleService roleService;

    @PersistenceContext
    private EntityManager entityManager;


    @Inject
    private SignupService signupService;


    @Inject
    private EncryptionService encryptionService;

    @Override
    public Application instance() {
        return entityManager.createQuery(
                "select a from Application a", Application.class)
                .getSingleResult();
    }

    @Override
    public Boolean isInitialized() {
        return entityManager.createQuery(
                "select count(a) from Application a " +
                        "where a.enabled = true", Long.class)
                .getSingleResult() == 1;
    }

    @Override
    public Application initialize(Application application) {
        if(!isInitialized()) {
            Collection<User> administrators = application.getAdministrators();
            if(administrators == null || administrators.size() == 0) {
                throw new ApplicationInitializationException(
                        "You must add at least one administrator " +
                                "or the application will not be accessible");
            }

            for(User u : administrators) {
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
        return new HashSet<>(entityManager.createQuery(
                "select u from User u " +
                "left join fetch u.roles as r" +
                " where r.authority = 'admin'",
                User.class).getResultList());
    }

    @Override
    public Boolean addAdministrator(User user) {
        final Role userRole = roleService.findOrCreate(
                DefaultRoles
                        .SITE_ADMINISTRATOR
                        .toRole()
        );
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
}
