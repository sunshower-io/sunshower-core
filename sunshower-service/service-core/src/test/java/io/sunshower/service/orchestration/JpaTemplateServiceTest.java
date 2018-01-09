package io.sunshower.service.orchestration;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.common.Identifier;
import io.sunshower.service.BaseRepository;
import io.sunshower.service.BaseRepositoryTest;
import io.sunshower.service.git.GitRepository;
import io.sunshower.service.git.RepositoryService;
import io.sunshower.service.graph.service.ContentResolver;
import io.sunshower.service.graph.service.GraphServiceResolver;
import io.sunshower.service.hal.core.*;
import io.sunshower.service.hal.core.contents.ContentHandler;
import io.sunshower.service.model.Property;
import io.sunshower.service.model.properties.StringProperty;
import io.sunshower.service.orchestration.model.Template;
import io.sunshower.service.orchestration.service.TemplateService;
import io.sunshower.service.serialization.DynamicResolvingMoxyJsonProvider;
import io.sunshower.service.workspace.model.Workspace;
import io.sunshower.service.workspace.service.WorkspaceService;
import io.sunshower.test.common.SerializationAware;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
public class JpaTemplateServiceTest extends BaseRepositoryTest<Identifier, Template> {

  @Inject private DynamicResolvingMoxyJsonProvider provider;

  @Inject private GraphServiceResolver resolver;

  @Inject private WorkspaceService workspaceService;

  @Inject private RepositoryService repositoryService;

  @Inject private TemplateService templateService;

  @Override
  protected Identifier randomId() {
    return Identifier.random();
  }

  public JpaTemplateServiceTest() {
    super(SerializationAware.Format.JSON, new Class<?>[] {StringProperty.class});
  }

  @Override
  protected Template randomEntity() {
    final Template template = new Template();
    template.setName(Identifier.random().toString());
    template.setKey(Identifier.random().toString());
    return template;
  }

  @Override
  protected void alter(Template random) {
    random.setName("Frapadapawap");
  }

  @Override
  @SuppressWarnings("unchecked")
  protected BaseRepository<Identifier, Template> service() {
    return (BaseRepository<Identifier, Template>) templateService;
  }

  @Override
  protected void expectAlteration(Identifier uuid, Template random) {
    assertThat(service().get(uuid).getName(), is("Frapadapawap"));
  }

  @Override
  protected void expectSameProperties(Template random, Template save) {
    assertThat(random.getName(), is(save.getName()));
    assertThat(random.getCreated(), is(save.getCreated()));
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureSavingSimpleEmptyGraphWorksForAdministrator() {
    final Template template = newTemplate();
    workspaceService.save(template.getWorkspace());
    templateService.saveGraph(template.getId(), new Graph());
    checkLock(template);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureRetrievingCurrentGraphAlwaysWorks() {

    final Template template = newTemplate();
    workspaceService.save(template.getWorkspace());
    templateService.getCurrentGraph(template.getId());

    checkLock(template);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureSavingGraphClosesRepository() {
    final Template template = newTemplate();
    final Graph g = new Graph();
    final Vertex v = new Vertex();
    g.addVertex(v);
    workspaceService.save(template.getWorkspace());
    templateService.saveGraph(template.getId(), g);
    GitRepository repo = repositoryService.open(template.getWorkspace().getRepository());
    assertThat(repo.isLocked(), is(false));
    checkLock(template);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureReadingGraphClosesRepository() {

    final Template template = newTemplate();
    final Graph g = new Graph();
    final Vertex v = new Vertex();
    g.addVertex(v);
    workspaceService.save(template.getWorkspace());
    templateService.getCurrentGraph(template.getId());
    checkLock(template);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureSavingGraphCreatesFile() {

    final Template template = newTemplate();
    final Graph g = new Graph();
    final Vertex v = new Vertex();
    g.addVertex(v);
    workspaceService.save(template.getWorkspace());
    templateService.saveGraph(template.getId(), g);
    GraphSummary summary = templateService.getSummary(template.getId());
    assertThat(summary, is(not(nullValue())));
    assertThat(summary.contains(v.getId()), is(true));

    checkLock(template);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureWorkspaceRepositoryLocalHasFile() {

    Template template = newTemplate();
    workspaceService.save(template.getWorkspace());
    assertThat(template.getWorkspace().getRepository().getLocal().getFile(), is(not(nullValue())));

    Workspace workspace = entityManager.find(Workspace.class, template.getWorkspace().getId());
    assertThat(
        workspace.getRepository().getLocal().getFile(),
        is(template.getWorkspace().getRepository().getLocal().getFile()));
    checkLock(template);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureSavingGraphWithSingleVertexWorks() {
    final Template template = newTemplate();
    Graph g = new Graph();
    g.addVertex(new Vertex());
    workspaceService.save(template.getWorkspace());
    templateService.saveGraph(template.getId(), g);
    Graph currentGraph = templateService.getCurrentGraph(template.getId());
    assertThat(currentGraph.getVertices().size(), is(1));
    checkLock(template);
  }

  @Test
  public void ensureServiceResolverResolvesTemplateServiceByTypeAndName() {
    TemplateService resolve = resolver.resolve(TemplateService.class, TemplateService.NAME);
    assertThat(resolve, is(notNullValue()));
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureContentIsSavedWithCorrectProperties() throws IOException, InterruptedException {
    Template template = newTemplate();
    workspaceService.save(template.getWorkspace());
    templateService.save(template);
    final Graph g = new Graph();
    final Vertex v = new Vertex();
    v.setId(Identifier.random());
    g.addVertex(v);

    Content content = new Content();
    content.setName("test.txt");
    content.setOrder(1);
    content.setMediaType(MediaType.TEXT_PLAIN);

    templateService.saveGraph(template.getId(), g);
    template = entityManager.find(Template.class, template.getId());

    ContentHandler contentHandler = templateService.contentManager(template.getId()).contentFor(v);

    contentHandler.addContent(content);
    assertThat(new File(content.getFile().getPath()).exists(), is(true));
    contentHandler.flush();

    contentHandler = templateService.contentManager(template.getId()).contentFor(v);

    ContentResolver resolve = contentHandler.resolve("test.txt");
    assertThat(resolve, is(not(nullValue())));
    checkLock(template);
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureContentForGraphAllowsPropertiesToBeSet() {
    provider.register(Graph.class, StringProperty.class);

    Template template = newTemplate();
    workspaceService.save(template.getWorkspace());
    templateService.save(template);
    final Graph g = new Graph();
    final Vertex v = new Vertex();
    v.setId(Identifier.random());
    g.addVertex(v);
    templateService.saveGraph(template.getId(), g);
    ContentHandler contentHandler =
        templateService
            .contentManager(template.getId())
            .graphContent()
            .setProperties(Collections.singleton(new StringProperty("hello")));
    contentHandler.close();

    Set<Property<?, ?>> properties =
        templateService.contentManager(template.getId()).graphContent().getProperties();
    assertThat(properties.size(), is(1));
  }

  @Test
  @WithUserDetails("administrator")
  public void ensureContentServiceApiMakesSense() throws IOException, InterruptedException {
    Template template = newTemplate();
    workspaceService.save(template.getWorkspace());
    templateService.save(template);
    final Graph g = new Graph();
    final Vertex v = new Vertex();
    v.setId(Identifier.random());
    g.addVertex(v);

    Content content = new Content();
    content.setName("test.txt");
    content.setOrder(1);
    content.setMediaType(MediaType.TEXT_PLAIN);

    templateService.saveGraph(template.getId(), g);
    template = entityManager.find(Template.class, template.getId());

    ContentHandler contentHandler = templateService.contentManager(template.getId()).contentFor(v);

    contentHandler.addContent(content);
    assertThat(new File(content.getFile().getPath()).exists(), is(true));
    ContentResolver resolve = contentHandler.resolve(content);
    resolve.write(Contents.openString("Hello"));
    contentHandler.close();
    contentHandler = templateService.contentManager(template.getId()).contentFor(v);
    resolve = contentHandler.resolve(content);
    assertThat(Contents.readString(resolve.read()), is("Hello"));
    contentHandler.close();
    checkLock(template);
  }

  private Template newTemplate() {
    final Template template = new Template();
    final Workspace workspace = new Workspace();
    workspace.setName("coolbeans");
    workspace.setKey("coolbeans");
    workspace.addTemplate(template);
    template.setKey("cool");
    template.setName("frapper");
    return template;
  }

  @BeforeEach
  public void setUp() {}

  private void checkLock(Template template) {
    GitRepository repo = repositoryService.open(template.getWorkspace().getRepository());
    assertThat(repo.isLocked(), is(false));
  }
}
