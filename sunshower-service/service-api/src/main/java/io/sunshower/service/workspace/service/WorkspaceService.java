package io.sunshower.service.workspace.service;

import io.sunshower.common.Identifier;
import io.sunshower.service.orchestration.model.Template;
import io.sunshower.service.repository.EntityRepository;
import io.sunshower.service.workspace.model.Workspace;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;

/** Created by haswell on 5/9/17. */
public interface WorkspaceService extends EntityRepository<Identifier, Workspace> {

  /**
   * @param workspaceId
   * @param template
   * @return
   */
  @PreAuthorize(
      "hasPermission(#workspaceId, 'io.sunshower.service.workspace.model.Workspace', 'WRITE')")
  Identifier addTemplate(Identifier workspaceId, Template template);

  @PreAuthorize(
      "hasPermission(#workspaceId, 'io.sunshower.service.workspace.model.Workspace', 'READ')")
  Set<Template> getTemplates(Identifier workspaceId);

  /**
   * @param templateId
   * @return
   */
  @PreAuthorize(
      "hasPermission(#templateId, 'io.sunshower.service.orchestration.model.Template', 'DELETE')")
  Template deleteTemplate(Identifier templateId);

  /**
   * @param id
   * @return
   */
  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.workspace.model.Workspace', 'READ')")
  Workspace get(Identifier id);

  /**
   * @param id
   * @return
   */
  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.workspace.model.Workspace', 'DELETE')")
  Workspace delete(Identifier id);
}
