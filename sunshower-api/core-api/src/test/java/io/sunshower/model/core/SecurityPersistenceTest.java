package io.sunshower.model.core;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.auth.Permission;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by haswell on 10/20/16.
 * //
 */
@ExtendWith(SpringExtension.class)
@RunWith(JUnitPlatform.class)
@ContextConfiguration(classes = {
        FlywayConfiguration.class,
        DataSourceConfiguration.class,
        HibernateConfiguration.class,
        PersistenceConfiguration.class,
        PersistenceTestConfiguration.class
})
@Transactional
@SpringBootTest
public class SecurityPersistenceTest {

    @PersistenceContext
    private EntityManager entityManager;


    @Test
    public void ensureSavingAUserWithNoRolesWorks() {
        User user = new User();
        user.setUsername("josiah");
        user.setPassword("testasdfasdf");
        user.getDetails().setEmailAddress("josiah@whatever");
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    public void ensureSavingAUserWithARoleWorks() {
        User user = new User();
        user.setUsername("Josiah");
        user.setPassword("Haswell");
        user.addRole(new Role("admin"));
        user.getDetails().setEmailAddress("josiah@whatever");
        entityManager.persist(user);
        entityManager.flush();
    }


    @Test
    public void ensureSavingAUserWithARoleWithAPermissionWorks() {

        User user = new User();
        user.setUsername("Josiah");
        user.setPassword("Haswell");
        user.addRole(new Role("admin")
                .addPermission(new Permission("cool")));
        user.getDetails().setEmailAddress("josiah@whatever");
        entityManager.persist(user);
        entityManager.flush();
    }
}
