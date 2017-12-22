package io.sunshower.service.workspace;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.BaseRepository;
import io.sunshower.service.git.RepositoryService;
import io.sunshower.service.model.io.FileResolutionStrategy;
import io.sunshower.service.orchestration.model.OrchestrationTemplate;
import io.sunshower.service.revision.model.Local;
import io.sunshower.service.revision.model.Repositories;
import io.sunshower.service.revision.model.Repository;
import io.sunshower.service.workspace.model.Workspace;
import io.sunshower.service.workspace.service.WorkspaceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.acls.domain.BasePermission.*;

/**
 * Created by haswell on 5/9/17.
 */
@Service
@Transactional
public class JpaWorkspaceService extends
        BaseRepository<Identifier, Workspace> implements
        WorkspaceService
{

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private FileResolutionStrategy repositoryResolutionStrategy;


    public JpaWorkspaceService() {
        super(Workspace.class, "Workspace");
    }


    @Override
    @PreAuthorize("hasAuthority('tenant:user')")
    public Workspace create(Workspace entity) {
        repositoryFor(entity);
        return super.create(entity);
    }


    @Override
    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.workspace.model.Workspace', 'READ')")
    public Workspace get(Identifier id) {
        return super.get(id);
    }

    @Override
    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.workspace.model.Workspace', 'DELETE')")
    public Workspace delete(Identifier id) {
        final Workspace workspace = super.delete(id);
        Repositories.delete(workspace.getRepository());
        return workspace;
    }


    @Override
    public Identifier addTemplate(Identifier workspaceId, OrchestrationTemplate template) {
        final Workspace ws = get(workspaceId);
        if (ws == null) {
            throw new EntityNotFoundException(String.format(
                    "No workspace identified by '%s' was found",
                    workspaceId)
            );
        }
        grant(
                OrchestrationTemplate.class,
                template,
                ADMINISTRATION,
                WRITE,
                READ,
                DELETE

        );
        ws.addOrchestrationTemplate(template);
        flush();
        return template.getId();
    }

    @Override
    public Set<OrchestrationTemplate> getTemplates(Identifier workspaceId) {
        return new LinkedHashSet<>(
                getEntityManager()
                        .createQuery(
                                "select a from Workspace w " +
                                        "join w.orchestrationTemplates a " +
                                        "join w.identity woid " +
                                        "join a.identity aoid " +
                                        "where woid.owner.username = :id " +
                                        "and aoid.owner.username = :id " +
                                        "and w.id = :wsid",
                                OrchestrationTemplate.class
                        ).setParameter("id", getSession().getUsername())
                        .setParameter("wsid", workspaceId.value())
                        .getResultList());


    }

    @Override
    public OrchestrationTemplate deleteTemplate(Identifier templateId) {

        EntityManager entityManager = getEntityManager();
        OrchestrationTemplate orchestrationTemplate = entityManager.find(
                OrchestrationTemplate.class,
                templateId
        );
        entityManager.remove(orchestrationTemplate);
        revokeAll(
                OrchestrationTemplate.class,
                orchestrationTemplate
        );

        return orchestrationTemplate;
    }

    private Repository repositoryFor(Workspace entity) {
        Repository repository = entity.getRepository();
        if(repository == null) {
            repository = createRepository(entity);
            entity.setRepository(repository);
        }
        return repository;
    }

    private Repository createRepository(Workspace entity) {


        final User user = getEntityManager().find(
                User.class,
                getSession().getId()
        );
        File resolve = repositoryResolutionStrategy.resolve(
                user.getTenant(),
                user,
                null
        );

        final Local local = new Local();

        final io.sunshower.model.core.io.File file =
                new io.sunshower.model.core.io.File(
                Paths.get(
                        resolve.getAbsolutePath()
                ).resolve(String.format(
                        "%s/local",
                        entity.getId())
                ).toString()

        );

        local.setFile(file);


        final Repository repository = new Repository();
        repository.setLocal(local);
        repositoryService.open(repository).initialize();
        return repository;
    }
}
