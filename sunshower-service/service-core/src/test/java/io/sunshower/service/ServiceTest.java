package io.sunshower.service;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.security.api.SecurityPersistenceConfiguration;
import io.sunshower.service.security.SecurityConfiguration;
import io.sunshower.test.common.TestConfigurationConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
  classes = {
    SecurityPersistenceConfiguration.class,
    TestConfigurationConfiguration.class,
    HibernateConfiguration.class,
    PersistenceConfiguration.class,
    CoreServiceConfiguration.class,
    DataSourceConfiguration.class,
    FlywayConfiguration.class,
    SecurityConfiguration.class,
    TestConfiguration.class,
  }
)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceTest {}
