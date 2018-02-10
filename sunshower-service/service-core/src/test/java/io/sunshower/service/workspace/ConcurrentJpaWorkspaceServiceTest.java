package io.sunshower.service.workspace;

import io.sunshower.service.workspace.model.Workspace;
import io.sunshower.service.workspace.service.WorkspaceService;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ConcurrentJpaWorkspaceServiceTest extends JpaWorkspaceServiceTest {
  @Inject private WorkspaceService workspaceService;

  @Test
  public void ensureRolesCanBeSavedOnMultipleTests() {
    // empty
  }

  @Test
  @WithUserDetails("non-admin")
  public void ensureNonAdminCanDeleteWorkspaceTransactionally() {
    Workspace workspace = new Workspace();
    workspace.setName("test1");
    workspace.setKey("test24");
    workspaceService.create(workspace);
    workspaceService.delete(workspace.getId());
    try {
      workspaceService.get(workspace.getId());
    } catch (Exception ex) {

    }
  }
}
