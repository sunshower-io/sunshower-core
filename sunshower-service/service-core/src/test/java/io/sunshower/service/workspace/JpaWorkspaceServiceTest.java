package io.sunshower.service.workspace;

import io.sunshower.common.Identifier;
import io.sunshower.service.BaseRepository;
import io.sunshower.service.BaseRepositoryTest;
import io.sunshower.service.orchestration.model.OrchestrationTemplate;
import io.sunshower.service.workspace.model.Workspace;
import io.sunshower.service.workspace.service.WorkspaceService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;


import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Created by haswell on 5/9/17.
 */
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class JpaWorkspaceServiceTest extends BaseRepositoryTest<Identifier, Workspace> {


    @Inject
    private WorkspaceService workspaceService;


    @Override
    protected Identifier randomId() {
        return Identifier.random();
    }


    @Override
    protected Workspace randomEntity() {
        Workspace workspace = new Workspace();
        workspace.setName(Identifier.random().toString());
        workspace.setKey(Identifier.random().toString());
        return workspace;
    }

    @Override
    protected void alter(Workspace random) {
        random.setName("justarandomworkspace");
    }

    protected OrchestrationTemplate randomTemplate() {
        OrchestrationTemplate orchestrationTemplate = new OrchestrationTemplate();
        orchestrationTemplate.setName(Identifier.random().toString());
        orchestrationTemplate.setKey(Identifier.random().toString());
        return orchestrationTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BaseRepository<Identifier, Workspace> service() {
        return (BaseRepository<Identifier, Workspace>) workspaceService;
    }


    @Override
    protected void expectAlteration(Identifier uuid, Workspace random) {
        assertThat(service().get(uuid).getName(), is("justarandomworkspace"));
    }

    @Override
    protected void expectSameProperties(Workspace random, Workspace save) {
        assertThat(random.getKey(), is(save.getKey()));
        assertThat(random.getName(), is(save.getName()));
        assertThat(random.getCreated(), is(save.getCreated()));
    }


    @Test
    @WithUserDetails("administrator")
    public void ensureDeletingWorkspaceRemovesDirectory() {
        Workspace workspace = randomEntity();
        Workspace save = service().save(workspace);

        final File file = new File(save
                .getRepository()
                .getLocal()
                .getFile()
                .getPath()
        );

        assertThat(file.exists(), is(true));
        assertThat(file.isDirectory(), is(true));

        workspaceService.delete(save.getId());
        assertThat(file.exists(), is(false));
    }

    @Test
    @WithUserDetails("administrator")
    public void ensureAddingTemplateToWorkspaceAsAdministratorResultsInTemplateAppearingInWorkspacesTemplateList() {
        OrchestrationTemplate template = randomTemplate();
        Workspace workspace = randomEntity();
        workspaceService.save(workspace);
        workspaceService.addTemplate(workspace.getId(), template);
        assertThat(workspaceService.getTemplates(workspace.getId()).size(), is(1));
    }
    

    @Test
    @WithUserDetails("non-admin")
    public void ensureAddingTemplateToWorkspaceAsTenantUserResultsInTemplateAppearingInWorkspacesTemplateList() {
        OrchestrationTemplate template = randomTemplate();
        Workspace workspace = randomEntity();
        workspaceService.save(workspace);
        workspaceService.addTemplate(workspace.getId(), template);
        assertThat(workspaceService.getTemplates(workspace.getId()).size(), is(1));
    }

    @Test
    @WithUserDetails("non-admin")
    public void ensureUsersCannotSeeOthersTemplates() {
        OrchestrationTemplate template = randomTemplate();
        Workspace workspace = randomEntity();
        workspaceService.save(workspace);
        workspaceService.addTemplate(workspace.getId(), template);
        changeSession("administrator", "password");
        assertThat(workspaceService.getTemplates(workspace.getId()).size(), is(0));
    }

    @Test
    @WithUserDetails("administrator")
    public void ensureAdministratorsCanDeleteTemplates() {
        OrchestrationTemplate template = randomTemplate();
        Workspace workspace = randomEntity();
        workspaceService.save(workspace);
        workspaceService.addTemplate(workspace.getId(), template);
        workspaceService.deleteTemplate(template.getId());
        assertThat(workspaceService.getTemplates(workspace.getId()).size(), is(0));
    }

    @Test
    @WithUserDetails("non-admin")
    public void ensureUsersWithoutAdminPrivilegesCanDeleteTemplates() {
        OrchestrationTemplate template = randomTemplate();
        Workspace workspace = randomEntity();
        workspaceService.save(workspace);
        workspaceService.addTemplate(workspace.getId(), template);
        workspaceService.deleteTemplate(template.getId());
        assertThat(workspaceService.getTemplates(workspace.getId()).size(), is(0));
    }

    @Test
    @WithUserDetails("administrator")
    public void ensureUsersCannotDeleteOtherUsersTemplates() {
        
        OrchestrationTemplate template = randomTemplate();
        Workspace workspace = randomEntity();
        workspaceService.save(workspace);
        workspaceService.addTemplate(workspace.getId(), template);
        changeSession("non-admin", "frapafadsfa");
        assertThrows(AccessDeniedException.class, () -> {
            workspaceService.deleteTemplate(template.getId());
        });
    }

    @Test
    @WithUserDetails("administrator")
    public void ensureGettingTemplatesOnlyRetrievesTemplatesFromThatWorkspace() {
        Workspace workspace = randomEntity();
        Workspace workspace2 = randomEntity();
        workspaceService.save(workspace);
        workspaceService.save(workspace2);
        OrchestrationTemplate template = randomTemplate();
        workspaceService.addTemplate(workspace.getId(), template);

        assertThat(workspaceService.getTemplates(workspace2.getId()).isEmpty(), is(true));

    }



}