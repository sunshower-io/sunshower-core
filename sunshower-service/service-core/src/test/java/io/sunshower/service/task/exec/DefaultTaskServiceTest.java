package io.sunshower.service.task.exec;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.Role;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.AuthenticatedTestCase;
import io.sunshower.service.graph.service.GraphService;
import io.sunshower.service.graph.service.TaskService;
import io.sunshower.service.hal.core.*;
import io.sunshower.service.hal.core.contents.ContentHandler;
import io.sunshower.service.model.task.ExecutionPlan;
import io.sunshower.service.orchestration.model.Template;
import io.sunshower.service.orchestration.service.TemplateService;
import io.sunshower.service.workspace.model.Workspace;
import io.sunshower.service.workspace.service.WorkspaceService;
import io.sunshower.test.persist.Authority;
import io.sunshower.test.persist.Principal;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

@WithUserDetails("administrator")
public class DefaultTaskServiceTest extends AuthenticatedTestCase {

  @Inject private TaskService taskService;

  @Inject private GraphService graphService;

  @Inject private GraphTransformer transformer;

  @Inject private ParallelTaskExecutor taskExecutor;

  @Inject private WorkspaceService workspaceService;

  @Inject private TemplateService templateService;

  @Inject private EncryptionService encryptionService;

  @Principal
  public User createUserPrincipal(@Authority("tenant:user") Role userRole) {
    User user = new User();
    user.setUsername("user");
    user.setPassword(encryptionService.encrypt("password"));
    user.getDetails().setEmailAddress("user123");
    user.addRole(userRole);
    user.setActive(true);
    return user;
  }

  @Test
  @WithUserDetails("user")
  public void ensureGraphContentsAddsStuff() {
    final Graph graph = new Graph();
    final Vertex fst = new Vertex();
    final Vertex snd = new Vertex();
    graph.addVertex(fst);
    graph.addVertex(snd);
    final Edge edge = new Edge();
    edge.setSource(fst);
    edge.setTarget(snd);
    graph.addEdge(edge);
    graph.addVertex(new Vertex());

    final Workspace workspace = new Workspace();
    workspace.setName("test");
    workspace.setKey("test");

    final Template template = new Template();
    template.setName("template");
    template.setKey("test2");

    workspace.addTemplate(template);
    workspaceService.create(workspace);
    templateService.saveGraph(template.getId(), graph);
    try (ContentHandler mgr =
        templateService.contentManager(template.getId()).contentFor(fst.getId(), Vertex.class)) {
      mgr.addContent(new Content(ContentType.Reference, "hello"))
          .addContent(new Content(ContentType.Reference, "world"));
    }

    ExecutionPlan fstPlan = taskService.plan(template.getId(), graphService).getExecutionPlan();
    System.out.println(fstPlan);
    assertThat(fstPlan.getLevels().size(), is(3));
  }

  @Test
  public void ensurePlanGetsExecuted() {

    final Graph graph = new Graph();
    final Vertex fst = new Vertex();
    final Vertex snd = new Vertex();
    graph.addVertex(fst);
    graph.addVertex(snd);
    final Edge edge = new Edge();
    edge.setSource(fst);
    edge.setTarget(snd);
    graph.addEdge(edge);
    graph.addVertex(new Vertex());

    final Workspace workspace = new Workspace();
    workspace.setName("test");
    workspace.setKey("test");

    final Template template = new Template();
    template.setName("template");
    template.setKey("test2");

    workspace.addTemplate(template);
    workspaceService.create(workspace);
    workspaceService.save(workspace);
    templateService.saveGraph(template.getId(), graph);

    ExecutionPlan fstPlan = taskService.plan(template.getId(), graphService).getExecutionPlan();
    System.out.println(fstPlan);
    assertThat(fstPlan.getLevels().size(), is(2));
  }
}
