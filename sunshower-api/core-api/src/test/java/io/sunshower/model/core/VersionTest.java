package io.sunshower.model.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

public class VersionTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureVersionCanBePersisted() {
    final Version version = new Version();
    version.setMajor(1);
    version.setMinor(2);
    version.setMinorMinor(3);
    version.setExtension("SNAPSHOT");
    entityManager.persist(version);

    assertThat(entityManager.find(Version.class, version.getId()).toString(), is("1.2.3-SNAPSHOT"));
  }
}
