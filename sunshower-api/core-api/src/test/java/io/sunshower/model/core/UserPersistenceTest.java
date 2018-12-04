package io.sunshower.model.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import io.sunshower.io.Files;
import io.sunshower.model.core.auth.*;
import io.sunshower.model.core.io.File;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Rollback
@Transactional
class UserPersistenceTest extends PersistenceTest {

    @PersistenceContext
    private EntityManager entityManager;
    private User          user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.getDetails().setEmailAddress("joe@134whatever.com");
        user.setUsername("frapasdfasdf");
        user.setPassword("asdfasdfasdfasfasdfasf");
    }

    @Test
    public void ensureEntityManagerIsInjected() {
        assertThat(entityManager, is(not(nullValue())));
    }

    @Test
    public void ensureSavingPersonWorks() {
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    public void ensureSettingConfigurationWorks() {
        assertThat(user.getConfiguration(), is(not(nullValue())));
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    public void ensureAddingConfigurationValueWorks() {
        user.setConfigurationValue(Property.Type.Boolean, "test", true);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void ensureOverridingAndPersistingTimeoutValueResultsInOverriddenValueBeingPreserved() {
        assertThat(user.getConfigurationValue(UserConfigurations.Keys.Timeout), is(60));
        user.setConfigurationValue(Property.Type.Integer, UserConfigurations.Keys.Timeout, 40);
        entityManager.persist(user);
        entityManager.flush();
        assertThat(user.getConfigurationValue(UserConfigurations.Keys.Timeout), is(40));
    }

    @Test
    void ensureAddingConfigurationSetsConfigurationCorrectly() {
        user.setConfigurationValue(Property.Type.Boolean, "test", true);
        entityManager.persist(user);
        entityManager.flush();
        boolean test = entityManager.find(User.class, user.getId()).getConfigurationValue("test");
        assertThat(test, is(true));
    }

    @Test
    @SneakyThrows
    void ensureImageCanBeSavedOnUser() {

        Image icon = new Image();
        icon.setData(Files.read(ClassLoader.getSystemResourceAsStream("icons/kubernetes.png")));
        user.getDetails().setImage(icon);

        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    public void ensurePersonIsSavedWithActiveFalse() {
        entityManager.persist(user);
        entityManager.flush();
        final User saved = entityManager.find(User.class, user.getId());
        assertFalse(saved.isEnabled());
    }

    @Test
    void ensureUserWithRoleCanBePersisted() {

        final Role role = new Role("coolbeans");
        user.addRole(role);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void ensureRoleCanBeRemoved() {
        final Role role = new Role("coolbeans");
        user.addRole(role);
        assertThat(user.getRoles().size(), is(1));
        assertThat(user.getRoles().stream().allMatch(t -> t.getUsers().size() == 1), is(true));
        user.removeRole(role);
        assertThat(user.getRoles().size(), is(0));
        assertThat(role.getUsers().size(), is(0));
    }

    @Test
    void ensureSavingUserDetailsFileWorks() {
        user.getDetails().setEmailAddress("joe@whatever.com2");
        user.setUsername("fraafp");
        user.setPassword("asdfasdfasdfasfadsfadf");
        user.getDetails().setRoot(new File("coolbeans"));
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void ensureRegisteredIsPersisted() {

        final Date date = new Date();
        user.getDetails().setRegistered(date);
        entityManager.persist(user);
        entityManager.flush();

        final User saved = entityManager.find(User.class, user.getId());

        assertThat(saved.getDetails().getRegistered(), is(date));
    }

    @Test
    @SneakyThrows
    void ensureTenantCascadesSaveFile() {
        final Tenant tenant = new Tenant();
        tenant.setName("coke2");
        tenant.setDetails(new TenantDetails());
        Image icon = new Image();
        icon.setData(Files.read(ClassLoader.getSystemResourceAsStream("icons/kubernetes.png")));
        tenant.getDetails().setImage(icon);

        tenant.addUser(user);
        user.getDetails().setImage(icon);

        final Tenant cokehr = new Tenant();
        cokehr.setName("cokeh2r");

        cokehr.setDetails(new TenantDetails());
        tenant.addChild(cokehr);
        tenant.getDetails().setRoot(new File("cool"));

        entityManager.persist(tenant);
        entityManager.flush();

        assertThat(
                entityManager.find(Tenant.class, tenant.getId()).getDetails().getRoot(),
                is(not(nullValue()))
        );

        assertThat(
                entityManager.find(Tenant.class, tenant.getId()).getDetails().getRoot().getPath(),
                is("cool")
        );
        assertThat(entityManager.find(Tenant.class, tenant.getId()).getUsers().size(), is(1));
        assertThat(entityManager.find(Tenant.class, tenant.getId()).getChildren().size(), is(1));
    }

    @Test
    public void ensureTenantCascadesSaveToComplexUser() {
        final Tenant tenant = new Tenant();
        tenant.setName("coke");
        tenant.addUser(user);

        final Tenant cokehr = new Tenant();
        cokehr.setName("cokehr");

        tenant.addChild(cokehr);

        entityManager.persist(tenant);
        entityManager.flush();

        assertThat(entityManager.find(Tenant.class, tenant.getId()).getUsers().size(), is(1));
        assertThat(entityManager.find(Tenant.class, tenant.getId()).getChildren().size(), is(1));
    }
}
