package io.sunshower.model.core.auth;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.model.core.PersistenceTestConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * Created by haswell on 5/10/17.
 */
@Transactional
@RunWith(SpringRunner.class)
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
public class GroupPersistenceTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void ensureGroupCanBePersisted() {
        final Group group = new Group();
        group.setName("frapper");
        entityManager.persist(group);
        entityManager.flush();
    }

}