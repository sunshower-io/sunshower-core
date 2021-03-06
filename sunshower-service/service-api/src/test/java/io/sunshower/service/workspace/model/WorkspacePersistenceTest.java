package io.sunshower.service.workspace.model;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.sunshower.service.PersistTestCase;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

public class WorkspacePersistenceTest extends PersistTestCase {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureWorkspaceWithNoNameOrKeyFailsValidation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> {
          final Workspace workspace = new Workspace();
          entityManager.persist(workspace);
          entityManager.flush();
        });
  }

  @Test
  public void ensureWorkspaceWithValidNameAndKeyCanBePersisted() {
    final Workspace workspace = new Workspace();
    workspace.setKey("workspace");
    workspace.setName("frapper");
    entityManager.persist(workspace);
    entityManager.flush();
  }

  @Test
  public void ensureLastUpdatedIsDifferentFromCreatedOnUpdate() {
    final Workspace workspace = new Workspace();
    workspace.setKey("workspace");
    workspace.setName("frapper");
    entityManager.persist(workspace);
    entityManager.flush();

    workspace.setName("coolbeans");
    entityManager.flush();
    assertThat(workspace.getModified(), is(not(workspace.getCreated())));
  }
}
