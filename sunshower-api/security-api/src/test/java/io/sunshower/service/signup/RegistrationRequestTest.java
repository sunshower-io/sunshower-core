package io.sunshower.service.signup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.model.core.auth.User;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.security.api.SecurityPersistenceConfiguration;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import io.sunshower.test.common.TestConfigurationConfiguration;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@RunWith(JUnitPlatform.class)
@ContextConfiguration(
  classes = {
    TestConfigurationConfiguration.class,
    FlywayConfiguration.class,
    HibernateConfiguration.class,
    DataSourceConfiguration.class,
    PersistenceConfiguration.class,
    SecurityPersistenceConfiguration.class,
    PersistenceTestConfiguration.class,
  }
)
public class RegistrationRequestTest extends SerializationTestCase {

  @PersistenceContext private EntityManager entityManager;

  public RegistrationRequestTest() {
    super(SerializationAware.Format.JSON, RegistrationRequest.class);
  }

  @Test
  void ensureProductsAreSavedCorrectly() {
    List<Product> products =
        Arrays.asList(
            new Product(
                "stratosphere:design",
                "Low-code Deployment across Clouds with Stratosphere:Design"),
            new Product(
                "stratosphere:discover",
                "Cross-cloud infrastructure "
                    + "discovery and management with Stratosphere:Discover"),
            new Product("anvil", "Cross-cloud Infrastructure Optimization"));
    for (Product product : products) {
      entityManager.persist(product);
    }
    assertThat(
        entityManager
            .createQuery("select p from Product p where p.name = 'stratosphere:discover'")
            .getResultList()
            .size(),
        is(1));
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

    final RegistrationRequest request = new RegistrationRequest(user);
    entityManager.persist(request);

    assertThat(
        entityManager.find(RegistrationRequest.class, request.getId()).getUser().getId(),
        is(user.getId()));
    entityManager.flush();
  }

  @Test
  public void ensureSelectingOnRegistrationRequestProducesExpectedValues() {
    entityManager.createQuery("select r from RegistrationRequest r").getResultList();
  }
}
