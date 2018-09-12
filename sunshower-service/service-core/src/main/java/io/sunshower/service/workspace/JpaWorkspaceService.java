package io.sunshower.service.workspace;

import static org.springframework.security.acls.domain.BasePermission.*;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.BaseRepository;
import io.sunshower.service.git.RepositoryService;
import io.sunshower.service.model.io.FileResolutionStrategy;
import io.sunshower.service.orchestration.model.Template;
import io.sunshower.service.revision.model.Local;
import io.sunshower.service.revision.model.Repositories;
import io.sunshower.service.revision.model.Repository;
import io.sunshower.service.workspace.model.Workspace;
import io.sunshower.service.workspace.service.WorkspaceService;
import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JpaWorkspaceService extends BaseRepository<Identifier, Workspace>
    implements WorkspaceService {

  @Inject private RepositoryService repositoryService;

  @Inject private FileResolutionStrategy repositoryResolutionStrategy;

  public JpaWorkspaceService() {
    super(Workspace.class, "Workspace");
  }

  @Override
  @PreAuthorize("hasAuthority('tenant:user')")
  public Workspace create(Workspace entity) {
    repositoryFor(entity);
    setPermissions(entity.getTemplates());
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
  public Identifier addTemplate(Identifier workspaceId, Template template) {
    final Workspace ws = get(workspaceId);
    if (ws == null) {
      throw new EntityNotFoundException(
          String.format("No workspace identified by '%s' was found", workspaceId));
    }
    grant(Template.class, template, ADMINISTRATION, WRITE, READ, DELETE);

    ws.addTemplate(template);
    flush();
    return template.getId();
  }

  @Override
  public Set<Template> getTemplates(Identifier workspaceId) {
    return new LinkedHashSet<>(
        getEntityManager()
            .createQuery(
                "select a from Workspace w "
                    + "join w.templates a "
                    + "join w.identity woid "
                    + "join a.identity aoid "
                    + "where woid.owner.username = :id "
                    + "and aoid.owner.username = :id "
                    + "and w.id = :wsid",
                Template.class)
            .setParameter("id", getSession().getUsername())
            .setParameter("wsid", workspaceId)
            .getResultList());
  }

  @Override
  public Template deleteTemplate(Identifier templateId) {

    EntityManager entityManager = getEntityManager();
    Template orchestrationTemplate = entityManager.find(Template.class, templateId);
    entityManager.remove(orchestrationTemplate);
    revokeAll(Template.class, orchestrationTemplate);

    return orchestrationTemplate;
  }

  private Repository repositoryFor(Workspace entity) {
    Repository repository = entity.getRepository();
    if (repository == null) {
      repository = createRepository(entity);
      entity.setRepository(repository);
    }
    return repository;
  }

  private Repository createRepository(Workspace entity) {

    final User user = getEntityManager().find(User.class, getSession().getId());
    File resolve = repositoryResolutionStrategy.resolve(user.getTenant(), user, null);

    final Local local = new Local();

    final io.sunshower.model.core.io.File file =
        new io.sunshower.model.core.io.File(
            Paths.get(resolve.getAbsolutePath())
                .resolve(String.format("%s/local", entity.getId()))
                .toString());

    local.setFile(file);

    final Repository repository = new Repository();
    repository.setLocal(local);
    repositoryService.open(repository).initialize();
    return repository;
  }

  private void setPermissions(Set<Template> templates) {
    if (templates != null) {
      for (Template template : templates) {
        grant(
            Template.class,
            template,
            BasePermission.ADMINISTRATION,
            BasePermission.WRITE,
            BasePermission.READ,
            BasePermission.DELETE);
      }
    }
  }
}
