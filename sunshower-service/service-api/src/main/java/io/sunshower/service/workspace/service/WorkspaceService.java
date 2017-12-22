package io.sunshower.service.workspace.service;

import io.sunshower.common.Identifier;
import io.sunshower.service.orchestration.model.OrchestrationTemplate;
import io.sunshower.service.repository.EntityRepository;
import io.sunshower.service.workspace.model.Workspace;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

/**
 * Created by haswell on 5/9/17.
 */
public interface WorkspaceService extends EntityRepository<Identifier, Workspace> {

    /**
     *
     * @param workspaceId
     * @param template
     * @return
     */
    @PreAuthorize("hasPermission(#workspaceId, 'io.sunshower.service.workspace.model.Workspace', 'WRITE')")
    Identifier addTemplate(
            Identifier workspaceId,
            OrchestrationTemplate template
    );


    @PreAuthorize("hasPermission(#workspaceId, 'io.sunshower.service.workspace.model.Workspace', 'READ')")
    Set<OrchestrationTemplate> getTemplates(Identifier workspaceId);

    /**
     *
     * @param templateId
     * @return
     */
    @PreAuthorize("hasPermission(#templateId, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'DELETE')")
    OrchestrationTemplate deleteTemplate(Identifier templateId);


    /**
     * @param id
     * @return
     */
    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.workspace.model.Workspace', 'READ')")
    Workspace get(Identifier id);


    /**
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.workspace.model.Workspace', 'DELETE')")
    Workspace delete(Identifier id);


}
