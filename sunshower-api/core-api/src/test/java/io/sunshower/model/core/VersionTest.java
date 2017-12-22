package io.sunshower.model.core;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
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
 * Created by haswell on 5/17/17.
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
public class VersionTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void ensureVersionCanBePersisted() {
        final Version version = new Version();
        version.setMajor(1);
        version.setMinor(2);
        version.setMinorMinor(3);
        version.setExtension("SNAPSHOT");
        entityManager.persist(version);

        assertThat(entityManager.find(
                Version.class,
                version.getId()).toString(),
                is("1.2.3-SNAPSHOT")
        );

    }

}