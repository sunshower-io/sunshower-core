package io.sunshower.service.security;

import io.io.sunshower.service.security.TestSecurityConfiguration;
import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.security.api.SecurityPersistenceConfiguration;
import io.sunshower.test.common.TestConfigurationConfiguration;
import io.sunshower.test.persist.AuthenticationTestExecutionListener;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
  classes = {
    TestConfigurationConfiguration.class,
    SecurityPersistenceConfiguration.class,
    SecurityConfiguration.class,
    HibernateConfiguration.class,
    DataSourceConfiguration.class,
    FlywayConfiguration.class,
    PersistenceConfiguration.class,
    TestSecurityConfiguration.class
  }
)
@Rollback
@Transactional
@TestExecutionListeners(
  listeners = {
    AuthenticationTestExecutionListener.class,
    WithSecurityContextTestExecutionListener.class
  },
  mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class SecurityTest {}
