package io.sunshower.model.core.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.model.core.PersistenceTest;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

public class RoleTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureSavingRoleWithChildrenWorks() {
    final Role parent = new Role("parent");
    parent.addChild(new Role("child").addChild(new Role("gchild")));
    entityManager.persist(parent);

    entityManager.flush();

    List<Role> roles =
        entityManager.createQuery("select r from Role as r", Role.class).getResultList();
    assertThat(roles.size(), is(3));
    Role g1 = roles.stream().filter(r -> r.getAuthority().equals("parent")).findFirst().get();

    assertThat(g1.getParent(), is(nullValue()));

    assertThat(g1.getChildren().size(), is(1));

    Role g2 = roles.stream().filter(r -> r.getAuthority().equals("child")).findFirst().get();

    assertThat(g2.getParent(), is(g1));
    assertThat(g1.getChildren().contains(g2), is(true));
    assertThat(g2.getChildren().size(), is(1));
  }
}
