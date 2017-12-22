package io.sunshower.service.signup;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.model.core.auth.User;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by haswell on 11/17/16.
 */
@Transactional
@ExtendWith(SpringExtension.class)
@RunWith(JUnitPlatform.class)
@ContextConfiguration(
        classes = {
                FlywayConfiguration.class,
                HibernateConfiguration.class,
                DataSourceConfiguration.class,
                PersistenceConfiguration.class,
                PersistenceTestConfiguration.class
        }
)
@SpringBootTest
public class RegistrationRequestTest extends SerializationTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    public RegistrationRequestTest() {
        super(SerializationAware.Format.JSON, RegistrationRequest.class);
    }

    @Test
    public void ensureSerializationRequestRequestIdIsSerialized() {
        final String id = "whatever";
        final RegistrationRequest request = new RegistrationRequest();
        request.setRequestId(id);
        final RegistrationRequest copy = copy(request);
        assertThat(id, is(copy.getRequestId()));

    }


    @Test
    public void ensurePersistingRegistrationRequestWithUserWorks() {

        final User user = new User();
        user.getDetails().setEmailAddress("joe@whatever.com");
        user.setUsername("frap");
        user.setPassword("adapadfasdf");

        final RegistrationRequest request =
                new RegistrationRequest(user);
        entityManager.persist(request);

        assertThat(entityManager.find(
                RegistrationRequest.class,
                request.getId()).getUser().getId(),
                is(user.getId()));
        entityManager.flush();
    }

    @Test
    public void ensureSelectingOnRegistrationRequestProducesExpectedValues() {
        entityManager.createQuery("select r from RegistrationRequest r").getResultList();
    }

}