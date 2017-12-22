package io.sunshower.service;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.service.cfg.ServicePersistenceConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@ContextConfiguration(classes = {
        HibernateConfiguration.class,
        PersistenceConfiguration.class,
        DataSourceConfiguration.class,
        FlywayConfiguration.class,
        PersistenceTestConfiguration.class,
        ServicePersistenceConfiguration.class
})
public abstract class PersistTestCase {

}
