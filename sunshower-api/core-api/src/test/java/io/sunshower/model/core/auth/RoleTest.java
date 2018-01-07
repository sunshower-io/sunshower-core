package io.sunshower.model.core.auth;

import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.PersistenceConfiguration;
import io.sunshower.model.core.PersistenceTestConfiguration;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

/**
 * Created by haswell on 2/19/17.
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
public class RoleTest {

    @PersistenceContext
    private EntityManager entityManager;


    @Test
    public void ensureSavingRoleWithChildrenWorks() {
        final Role parent = new Role("parent");
        parent.addChild(new Role("child").addChild(new Role("gchild")));
        entityManager.persist(parent);

        entityManager.flush();

        List<Role> roles = entityManager.createQuery(
                "select r from Role as r",
                Role.class
        ).getResultList();
        assertThat(roles.size(), is(3));
        Role g1 = roles.stream().filter(
                r -> r.getAuthority().equals("parent")
        ).findFirst().get();


        assertThat(g1.getParent(), is(nullValue()));

        assertThat(g1.getChildren().size(), is(1));

        Role g2 = roles.stream().filter(
                r -> r.getAuthority()
                        .equals("child")
        ).findFirst().get();

        assertThat(g2.getParent(), is(g1));
        assertThat(g1.getChildren().contains(g2), is(true));
        assertThat(g2.getChildren().size(), is(1));

    }


}