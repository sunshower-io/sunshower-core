package io.sunshower.service.security.user;

import io.sunshower.common.Identifier;
import io.sunshower.core.security.UserService;
import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.persistence.Dialect;
import io.sunshower.service.security.SecurityConfiguration;
import io.io.sunshower.service.security.TestSecurityConfiguration;
import io.sunshower.test.persist.AuthenticationTestExecutionListener;
import io.sunshower.test.persist.Principal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


/**
 * Created by haswell on 10/18/16.
 */
@Rollback
@Transactional(isolation =  Isolation.REPEATABLE_READ)
@ContextConfiguration(classes = {
        HibernateConfiguration.class,
        SecurityConfiguration.class,
        FlywayConfiguration.class,
        DataSourceConfiguration.class,
        PersistenceConfiguration.class,
        TestSecurityConfiguration.class
})
@TestExecutionListeners(
        listeners =
                AuthenticationTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@SpringBootTest
@RunWith(SpringRunner.class)
public class DefaultUserServiceTest {
    
    @Inject
    private Dialect dialect;


    @Inject
    private UserService userService;


    @PersistenceContext
    private EntityManager entityManager;


    @Principal
    @Transactional
    public User testUser() {
        final User user = new User(
                Identifier.random(),
                "josiah22",
                "coolbeans"
        );
        user.getDetails().setEmailAddress("joe@email.com3242adf");
        user.addRole(new Role("frap"));
        user.setActive(true);
        return user;
    }


    @Test
    @WithMockUser(authorities = "admin")
    public void ensureListingActiveUsersReturnsNoInactiveUsers() {
        final User u = createUser(false);
        entityManager.persist(u);
        assertThat(userService.activeUsers().size(), is(1));
    }

    
    @Test
    @WithMockUser(authorities = "admin")
    public void ensureListingInactiveUsersReturnsInactiveUser() {
        final User u = createUser(false);
        entityManager.persist(u);
        assertThat(userService.inactiveUsers().size(), is(1));
    }

    @Test
    @WithMockUser(authorities = "admin")
    public void ensureListingActiveUsersReturnsActiveUsers() {
        final User u = createUser(true);
        entityManager.persist(u);
        assertThat(userService.activeUsers().size(), is(2));
        assertThat(userService
                .activeUsers()
                .stream()
                .allMatch(User::isActive), is(true)
        );
    }

    @Test
    public void ensureFindByUsernameFails() {
        userService.findByUsername("josiah22");
    }

    @Test
    public void ensureDialectIsInjected() {
        assertThat(dialect, is(not(nullValue())));
    }


    @Test
    public void ensureFindByUsernameFindsUserByExactMatch() {
        final User user = new User(Identifier.random(), "josiah2", "coolbeans");
        user.getDetails().setEmailAddress("joe@email.com3242");
        entityManager.persist(user);

        User saved = userService.findByUsername("josiah2");
        assertThat(saved, is(not(nullValue())));
    }

    private User createUser(boolean b) {
        final User u = new User();
        u.setActive(b);
        u.setUsername(UUID.randomUUID().toString());
        u.setPassword(UUID.randomUUID().toString());
        u.getDetails().setEmailAddress(UUID.randomUUID().toString());
        return u;
    }
}