package io.sunshower.model.core;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by haswell on 5/22/17.
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
public abstract class PersistenceTest {
}
