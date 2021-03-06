package io.sunshower.service.revision.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.model.core.io.File;
import io.sunshower.service.PersistTestCase;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

public class RepositoryPersistenceTest extends PersistTestCase {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureSavingRepositoryCascadesSaveToRepositoryFilePath() {
    final Repository repository = new Repository();

    final Local local = new Local();
    local.setFile(new File("Frap"));
    repository.setLocal(local);

    entityManager.persist(repository);

    entityManager.flush();
    Repository saved = entityManager.find(Repository.class, repository.getId());
    assertThat(saved.getLocal().getFile().getPath(), is("Frap"));
  }
}
