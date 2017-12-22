package io.io.sunshower.service.security.web;

import io.sunshower.core.security.AuthenticationService;
import io.sunshower.core.security.InvalidTokenException;
import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.model.core.auth.Token;
import io.sunshower.model.core.auth.User;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.security.api.SecurityPersistenceConfiguration;
import io.sunshower.service.security.SecurityConfiguration;
import io.sunshower.service.signup.SignupService;
import io.io.sunshower.service.security.TestSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.fail;

@ExtendWith(SpringExtension.class)
@RunWith(JUnitPlatform.class)
@ContextConfiguration(
        classes = {
                FlywayConfiguration.class,
                SecurityConfiguration.class,
                HibernateConfiguration.class,
                DataSourceConfiguration.class,
                PersistenceConfiguration.class,
                TestSecurityConfiguration.class,
                SecurityPersistenceConfiguration.class
        })
@Transactional
@SpringBootTest
@WebAppConfiguration
public class RESTSecurityTest {


    static Logger logger = Logger.getLogger(RESTSecurityTest.class.getName());



    @Inject
    private SignupService signupService;

    @Inject
    private SignupService authenticatedSignupService;

    @Inject
    private AuthenticationService authenticationService;


    @Test
    public void ensureAttemptingToAccessValidateThrowsException() throws InterruptedException {
        try {
            authenticationService.validate(new Token("frap", null));
            fail("Expected exception");
        } catch (InvalidTokenException ex) {
        }
    }


    @Test
    public void ensureAttemptingToAccessSecuredEndpointFails() throws InterruptedException {
        User u = new User();
        u.setUsername("josiah");
        u.setPassword("password");
        u.getDetails().setEmailAddress("joe16@email.com");
        signupService.signup(u);

        try {

            signupService.list();
            fail("Not authenticated");
        } catch(AuthenticationCredentialsNotFoundException ex) {

        }
    }


    @Test
    @WithMockUser(
            username = "user",
            password = "cool",
            authorities = "admin"
    )
    public void ensureAttemptingToAccessSecuredEndpointAfterAuthenticationSucceeds() {
        List<User> users = authenticatedSignupService.list();

    }


    @Test
    @WithMockUser(
            username = "user",
            password = "cool",
            authorities = "admin"
    )
    public void ensureMultipleClientsCantAccessSimultaneously() {
        signupService.list();
    }


}
