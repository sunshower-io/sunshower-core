package io.sunshower.service.security;

import io.sunshower.core.security.AuthenticationService;
import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.service.signup.SignupService;
import io.io.sunshower.service.security.TestSecurityConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

/**
 * Created by haswell on 10/24/16.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {
                SecurityConfiguration.class,
                HibernateConfiguration.class,
                TestSecurityConfiguration.class,
                FlywayConfiguration.class,
                DataSourceConfiguration.class
        })
@TestExecutionListeners(listeners = {
        ServletTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class
})
@Rollback
@Transactional
public class DefaultAuthenticationServiceTest {

    @Inject
    private SignupService signupService;

    @Inject
    private AuthenticationService authenticationService;

    @Test
    public void ensureAuthenticationWorks() {
        assertThat(authenticationService, is(not(nullValue())));

    }


}