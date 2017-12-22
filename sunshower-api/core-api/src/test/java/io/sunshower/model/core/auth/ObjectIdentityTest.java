package io.sunshower.model.core.auth;

import io.sunshower.common.Identifier;
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


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

/**
 * Created by haswell on 5/11/17.
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
public class ObjectIdentityTest {


    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void ensureDeletingObjectIdentityWorks() {
        final ObjectIdentity oid = new ObjectIdentity();
        oid.setObject(new SecuredObject(Void.class));
        oid.setReference(Identifier.random());
        entityManager.persist(oid);
        entityManager.flush();
        entityManager.remove(oid);
        ObjectIdentity objectIdentity = entityManager.find(
                ObjectIdentity.class,
                oid.getId()
        );
        assertThat(objectIdentity, is(nullValue()));
    }

}