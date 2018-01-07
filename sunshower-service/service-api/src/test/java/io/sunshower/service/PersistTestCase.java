package io.sunshower.service;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.service.cfg.ServicePersistenceConfiguration;
import io.sunshower.test.common.TestConfigurationConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        PersistenceConfiguration.class,
        HibernateConfiguration.class,
        DataSourceConfiguration.class,
        FlywayConfiguration.class,
        ServicePersistenceConfiguration.class,
        TestConfigurationConfiguration.class,
        PersistenceTestConfiguration.class
})
public abstract class PersistTestCase {

}
