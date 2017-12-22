package io.sunshower.service.orchestration;

import io.sunshower.service.AuthenticatedTestCase;
import io.sunshower.service.graph.service.ContentResolver;
import io.sunshower.service.hal.core.Content;
import io.sunshower.service.hal.core.Contents;
import io.sunshower.service.hal.core.Graph;
import io.sunshower.service.hal.core.Vertex;
import io.sunshower.service.hal.core.contents.ContentHandler;
import io.sunshower.service.orchestration.model.OrchestrationTemplate;
import io.sunshower.service.orchestration.service.OrchestrationTemplateService;
import io.sunshower.service.workspace.model.Workspace;
import io.sunshower.service.workspace.service.WorkspaceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WithUserDetails("administrator")
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class TemplateGraphTests extends AuthenticatedTestCase {


    private Workspace workspace;

    private OrchestrationTemplate template;

    private Graph persistentGraph;

    private Vertex clusterNode;

    private Vertex testScript;

    private Vertex otherNode;

    private Content clusterDefinition;

    private Content setupRuby;

    private Content execGroovy;

    @Inject
    private WorkspaceService workspaceService;

    @Inject
    private OrchestrationTemplateService templateService;

    final Lock lock = new ReentrantLock();

    @BeforeEach
    public void setUp() {
        lock.lock();
        createTemplateAndWorkspace();
        createVertices();
        createContents();
        createGraphs();
    }
    
    @AfterEach
    public void tearDown() {
        lock.unlock();
    }


    @Test
    public void ensureAddingContentOnOneNodeDoesNotModifyOtherNode() {

        persistentGraph.addVertex(clusterNode);
        doSave();
        OrchestrationTemplate template = entityManager.find(
                OrchestrationTemplate.class,
                this.template.getId()
        );
        ContentHandler clusterNodeHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode)
                .addContent(clusterDefinition);
        ContentHandler testScript = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode);

        assertThat(clusterNodeHandler.list().size(), is(1));
        assertThat(testScript.list().size(), is(0));
    }
    
    
    @Test
    public void ensureFindingContentByNameWorks() throws IOException {
        persistentGraph.addVertex(clusterNode);
        doSave();
        OrchestrationTemplate template = entityManager.find(
                OrchestrationTemplate.class,
                this.template.getId()
        );
        ContentHandler clusterNodeHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode)
                .addContent(clusterDefinition);

        ContentResolver resolve = clusterNodeHandler
                .resolve(clusterDefinition.getName());
        resolve.write("Hello, world!");
        clusterNodeHandler.close();


        resolve = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode).resolve(clusterDefinition.getName());
        
        assertThat(Contents.readString(resolve.read()), is("Hello, world!"));
    }
    
    @Test
    public void ensureWritingContentWorks() {

        persistentGraph.addVertex(clusterNode);
        doSave();
        OrchestrationTemplate template = entityManager.find(
                OrchestrationTemplate.class,
                this.template.getId()
        );
        ContentHandler clusterNodeHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode)
                .addContent(clusterDefinition);

        clusterNodeHandler.resolve(clusterDefinition).write("Hello, world!");
        clusterNodeHandler.close();
        
    }

    @Test
    public void ensureSavingContentReplacesContent() throws IOException {

        persistentGraph.addVertex(clusterNode);
        doSave();
        OrchestrationTemplate template = entityManager.find(
                OrchestrationTemplate.class,
                this.template.getId()
        );
        ContentHandler clusterNodeHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode)
                .addContent(clusterDefinition);

        clusterNodeHandler.resolve(clusterDefinition).write("Hello, world!");
        clusterNodeHandler.close();

        clusterNodeHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode);

        String result = Contents.readString(clusterNodeHandler.resolve(clusterDefinition).read());
        assertThat(result, is("Hello, world!"));
        clusterNodeHandler.close();

        clusterNodeHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode);
        clusterNodeHandler.resolve(clusterDefinition).write("Frapper");
        clusterNodeHandler.close();

        clusterNodeHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode);

        result = Contents.readString(clusterNodeHandler.resolve(clusterDefinition).read());
        assertThat(result, is("Frapper"));
        clusterNodeHandler.close();
    }


    @Test
    public void ensureSavingThenRemovingThenResolvingContentProducesNonIllegalStateException() throws IOException {
        assertThrows(IllegalStateException.class, () -> {
            persistentGraph.addVertex(clusterNode);
            doSave();
            OrchestrationTemplate template = entityManager.find(
                    OrchestrationTemplate.class,
                    this.template.getId()
            );
            ContentHandler contentHandler = templateService
                    .contentManager(template.getId())
                    .contentFor(clusterNode)
                    .addContent(clusterDefinition);

            contentHandler.resolve(clusterDefinition).write("Hello");
            contentHandler.removeContent(clusterDefinition);
            contentHandler.resolve(clusterDefinition);
        });
    }


    @Test
    public void ensureClearingContentDeletesAllDirectories() {

        persistentGraph.addVertex(clusterNode);
        doSave();
        OrchestrationTemplate template = entityManager.find(
                OrchestrationTemplate.class,
                this.template.getId()
        );
        ContentHandler contentHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode)
                .addContent(clusterDefinition)
                .addContent(execGroovy);

        Set<File> existing = contentHandler
                .list()
                .stream()
                .map(t -> new File(t.getFile().getPath()))
                .collect(Collectors.toSet());

        contentHandler.destroy();

        boolean anyThere = existing.stream().noneMatch(File::exists);
        assertThat(anyThere, is(true));
    }



    @Test
    public void ensureRemovingContentOnExistingNodeByIdRemovesSpecifiedContentButNotOtherContent() {
        persistentGraph.addVertex(clusterNode);
        doSave();
        OrchestrationTemplate template = entityManager.find(
                OrchestrationTemplate.class,
                this.template.getId()
        );
        ContentHandler contentHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode.getId(), Vertex.class)
                .addContent(clusterDefinition)
                .addContent(execGroovy);

        assertThat(contentHandler.list().size(), is(2));

        contentHandler.close();

        contentHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode)
                .removeContent(clusterDefinition);
        assertThat(contentHandler.list().contains(execGroovy), is(true));
        assertThat(contentHandler.list().contains(clusterDefinition), is(false));
    }



    @Test
    public void ensureRemovingContentOnExistingNodeRemovesSpecifiedContentButNotOtherContent() {
        persistentGraph.addVertex(clusterNode);
        doSave();
        OrchestrationTemplate template = entityManager.find(
                OrchestrationTemplate.class,
                this.template.getId()
        );
        ContentHandler contentHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode)
                .addContent(clusterDefinition)
                .addContent(execGroovy);

        assertThat(contentHandler.list().size(), is(2));

        contentHandler.close();

        contentHandler = templateService
                .contentManager(template.getId())
                .contentFor(clusterNode)
                .removeContent(clusterDefinition);
        assertThat(contentHandler.list().contains(execGroovy), is(true));
        assertThat(contentHandler.list().contains(clusterDefinition), is(false));
    }

    private void doSave() {
        workspaceService.save(workspace);
        templateService.save(template);
        templateService.saveGraph(template.getId(), persistentGraph);
    }

    private void createContents() {
        clusterDefinition = contents("test.txt", MediaType.TEXT_PLAIN);
        setupRuby = contents("setup.ruby", MediaType.TEXT_PLAIN);
        execGroovy = contents("setup.groovy", MediaType.TEXT_PLAIN);
    }

    private Content contents(String s, String textPlain) {
        final Content content = new Content();
        content.setName(s);
        content.setMediaType(textPlain);
        return content;
    }

    private void createVertices() {
        clusterNode = new Vertex();
        otherNode = new Vertex();
        testScript = new Vertex();
    }

    private void createTemplateAndWorkspace() {
        template = new OrchestrationTemplate();
        workspace = new Workspace();
        workspace.setName("coolbeans");
        workspace.setKey("coolbeans");
        workspace.addOrchestrationTemplate(template);
        template.setKey("cool");
        template.setName("frapper");
    }

    private void createGraphs() {
        persistentGraph = new Graph();
    }
}
