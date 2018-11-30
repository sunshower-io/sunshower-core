package io.sunshower.service.orchestration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.sunshower.common.Identifier;
import io.sunshower.model.core.AbstractProperty;
import io.sunshower.model.core.faults.SystemException;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.service.BaseRepository;
import io.sunshower.service.git.GitRepository;
import io.sunshower.service.git.RepositoryService;
import io.sunshower.service.hal.core.*;
import io.sunshower.service.hal.core.contents.ContentManager;
import io.sunshower.service.model.LineMessage;
import io.sunshower.service.model.Link;
import io.sunshower.service.model.Message;
import io.sunshower.service.orchestration.model.*;
import io.sunshower.service.orchestration.service.TemplateService;
import io.sunshower.service.revision.model.Revision;
import io.sunshower.service.security.AuthenticationSession;
import io.sunshower.service.workspace.model.Workspace;
import io.sunshower.service.workspace.service.WorkspaceService;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.junit.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JpaTemplateService extends BaseRepository<Identifier, Template>
    implements TemplateService {

  public static final String SUMMARY_PATH = "orchestrations/%s/summary.sgs";

  final Subject<TemplateEvent> topic;

  @Inject private WorkspaceService workspaceService;

  @Inject private RepositoryService repositoryService;

  @Inject private GraphSerializationContext graphContext;

  @Inject private AuthenticationSession session;

  @Inject private PlatformTransactionManager transactionManager;

  public JpaTemplateService() {
    super(Template.class, "Template");
    this.topic = PublishSubject.create();
  }

  @Test
  public void ensureSessionIsInjected() {
    assertThat(session, is(not(nullValue())));
  }

  @Override
  public Graph getGraph(@P("id") Identifier id, Revision revision) {
    final WorkspaceRef wsref = resolveWorkspace(id);
    final Workspace ws = wsref.workspace;
    try (GitRepository open = repositoryService.open(ws.getRepository())) {
      open.checkout(revision);
      final String path = String.format("orchestrations/%s/orchestration.hsf", id);
      return graphContext.load(open.read(path));
      // TODO figure out what sort of exceptions can be thrown here
    } catch (Exception e) {
      throw new SystemException(e);
    }
  }

  @Override
  public ContentManager contentManager(Identifier templateId) {
    final WorkspaceRef wsref = resolveWorkspace(templateId);
    final Workspace ws = wsref.workspace;
    final Identifier id = wsref.template.getId();
    try (GitRepository open = repositoryService.open(ws.getRepository())) {
      open.open();
      return new JpaContentManager(
          topic, open, getEntityManager(), wsref.template, graphContext, transactionManager);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public GraphSummary getSummary(Identifier id, Revision revision) {
    return getSummary(id);
  }

  @Override
  public GraphSummary getSummary(Identifier tid) {
    final WorkspaceRef wsref = resolveWorkspace(tid);
    final Workspace ws = wsref.workspace;
    final Identifier id = wsref.template.getId();
    try (GitRepository open = repositoryService.open(ws.getRepository())) {
      open.open();
      final String sid = id.toString();
      return readSummary(open, sid);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Graph getCurrentGraph(Identifier tid) {
    final WorkspaceRef wsref = resolveWorkspace(tid);
    final Workspace ws = wsref.workspace;
    final Identifier id = wsref.template.getId();
    try (GitRepository open = repositoryService.open(ws.getRepository())) {
      open.open();

      final String path = String.format("orchestrations/%s/orchestration.hsf", id);
      if (!open.exists(path)) {
        open.write(path, graphContext.open(new Graph()));
      }
      try (InputStream read = open.read(path)) {
        return graphContext.load(read);
      }
    } catch (Exception e) {
      throw new SystemException(e);
    }
  }

  @Override
  public void saveGraph(Identifier tid, Graph graph) {
    final WorkspaceRef wsref = resolveWorkspace(tid);
    final Workspace ws = wsref.workspace;
    final Identifier id = wsref.template.getId();
    try (GitRepository open = repositoryService.open(ws.getRepository())) {
      open.open();
      final String sid = id.toString();
      writeSummary(open, sid, graph);
      final String path = String.format("orchestrations/%s/orchestration.hsf", sid);
      if (open.exists(path)) {
        try (InputStream read = open.read(path)) {
          final Graph existing = graphContext.load(read);
          copyProperties(existing, graph);
        }
      }
      try (InputStream gis = graphContext.open(graph)) {
        open.write(path, gis);
      }
    } catch (Exception e) {
      throw new SystemException(e);
    }
  }

  private void copyProperties(Graph existing, Graph graph) {
    final Map<Identifier, Vertex> existingVertices =
        existing
            .getVertices()
            .stream()
            .collect(Collectors.toMap(DistributableEntity::getId, Function.identity()));

    final Map<Identifier, Edge> existingEdges =
        existing
            .getEdges()
            .stream()
            .collect(Collectors.toMap(DistributableEntity::getId, Function.identity()));

    for (Edge newEdge : graph.getEdges()) {
      final Edge e = existingEdges.get(newEdge.getId());
      if (e != null) {
        for (Content c : e.getContents()) {
          newEdge.addContent(c);
        }
      }
    }

    for (Vertex newVertex : graph.getVertices()) {
      final Vertex v = existingVertices.get(newVertex.getId());
      if (v != null) {
        for (Content c : v.getContents()) {
          newVertex.addContent(c);
        }
      }
    }
  }

  @Override
  public Revision commit(Identifier id) {
    return commit(id, new LineMessage("Saving orchestration template at: " + new Date()));
  }

  @Override
  public Revision commit(Identifier id, Message message) {
    final Workspace ws = getWorkspace(id);
    try (GitRepository open = repositoryService.open(ws.getRepository())) {
      return open.commit(message.getMessage());
    } catch (Exception e) {
      throw new SystemException(e);
    }
  }

  @Override
  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'READ')")
  public Template get(Identifier id) {
    return super.get(id);
  }

  @Override
  public Set<Revision> getRevisions(Identifier id) {
    return Collections.emptySet();
  }

  @Override
  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'DELETE')")
  public Template delete(Identifier id) {
    return super.delete(id);
  }

  @Override
  public Template link(Identifier id, Template template) {
    final Template source = get(id);
    source.getWorkspace().addTemplate(template);
    save(template);
    final TemplateLink link = new TemplateLink(source, template);
    getEntityManager().persist(link);
    return template;
  }

  @Override
  public Template link(Identifier id, Identifier tid) {
    final Template source = get(id);
    final Template target = get(tid);
    final TemplateLink link = new TemplateLink(source, target);
    getEntityManager().persist(link);
    return source;
  }

  private Workspace getWorkspace(Identifier id) {
    return get(id).getWorkspace();
  }

  private GraphSummary readSummary(GitRepository open, String sid) throws IOException {
    final String path = String.format(SUMMARY_PATH, sid);
    try (InputStream read = open.read(path)) {
      return graphContext.loadSummary(read);
    }
  }

  private void writeSummary(GitRepository open, String sid, Graph graph) {
    final String path = String.format(SUMMARY_PATH, sid);
    open.write(path, graphContext.openSummary(graph));
  }

  private WorkspaceRef resolveWorkspace(Identifier templateId) {

    Template template = get(templateId);
    Link<Template, Template> link = template.getLink();
    while (link != null) {
      template = link.getSource();
      link = template.getLink();
    }
    return new WorkspaceRef(template.getWorkspace(), template);
  }

  @Override
  public void subscribe(Observer<? super TemplateEvent> observer) {
    topic.subscribe(observer);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<AbstractProperty> getProperties(@P("id") Identifier tid) {
    final WorkspaceRef wsref = resolveWorkspace(tid);
    final Workspace ws = wsref.workspace;
    final Identifier id = wsref.template.getId();
    try (GitRepository open = repositoryService.open(ws.getRepository())) {
      open.open();
      final String sid = id.toString();
      final Set<AbstractProperty> result = new LinkedHashSet<>();
      final String path = String.format("orchestrations/%s/orchestration.hsf", sid);

      final Graph graph = graphContext.load(open.read(path));
      for (Vertex v : graph.getVertices()) {
        result.addAll(v.getProperties());
        Set<Content> contents = v.getContents();
        if (contents != null) {
          for (Content c : contents) {
            result.addAll(c.getProperties());
          }
        }
      }

      for (Edge v : graph.getEdges()) {
        result.addAll(v.getProperties());
        Set<Content> contents = v.getContents();
        if (contents != null) {
          for (Content c : contents) {
            result.addAll(c.getProperties());
          }
        }
      }
      result.addAll(graph.getProperties());
      return new ArrayList<>(result);
    } catch (Exception e) {
      throw new SystemException(e);
    }
  }

  static class WorkspaceRef {
    final Workspace workspace;
    final Template template;

    WorkspaceRef(Workspace workspace, Template template) {
      this.workspace = workspace;
      this.template = template;
    }
  }
}
