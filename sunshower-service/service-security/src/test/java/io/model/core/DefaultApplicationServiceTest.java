package io.model.core;

import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.Application;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.ApplicationService;
import io.sunshower.service.security.SecurityTest;
import io.sunshower.service.security.TokenAuthenticationFilter;
import io.io.sunshower.service.security.TestSecureService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;

import java.io.IOException;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

class DefaultApplicationServiceTest extends SecurityTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private TestSecureService testSecureService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private EncryptionService encryptionService;

    @Inject
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Test
    public void ensureApplicationCanBePersistedCorrectly() {
        final Application application = new Application();
        entityManager.persist(application);
        entityManager.flush();
    }


    @Test
    @Transactional
    public void ensureSayHelloAdminWorks() throws IOException {
        final User user = new User();
        user.setPassword("frapasdfasdf");
        user.setUsername("joe@email.com3242");
        user.getDetails().setEmailAddress("joe@email.com3242");

        applicationService.addAdministrator(user);
        String token = encryptionService.createToken(user);

        final ContainerRequestContext context = Mockito.mock(ContainerRequestContext.class);
        given(context.getHeaderString(
                TokenAuthenticationFilter.HEADER_KEY))
                .willReturn(token);
        tokenAuthenticationFilter.filter(context);
        testSecureService.sayHelloTenantUser();
    }


    @Test
    public void ensureApplicationCanBeInitializedCorrectly() {
        Application app = new Application();
        final User u = new User();
        u.setUsername("Josiah");
        u.setPassword("Haswell");
        u.getDetails().setEmailAddress("josiah@sunshower.io");

        assertFalse(applicationService.isInitialized());

        app.addAdministrator(u);
        applicationService.initialize(app);
        assertTrue(applicationService.isInitialized());
    }

    @Test
    public void ensureInitializedApplicationHasCorrectUsers() {

        Application app = new Application();
        final User u = new User();
        u.setUsername("Josiah");
        u.setPassword("Haswell");
        u.getDetails().setEmailAddress("josiah@sunshower.io");

        app.addAdministrator(u);
        applicationService.initialize(app);
        assertThat(applicationService.getAdministrators().size(), is(1));

        Set<User> admins = applicationService.getAdministrators();
        User admin = admins.iterator().next();
        assertThat(admin.getAuthorities().size(), is(1));

    }


}
