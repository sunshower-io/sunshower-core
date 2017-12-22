package io.sunshower.service.orchestration.model;

import io.sunshower.service.PersistTestCase;
import io.sunshower.service.model.LinkageMode;
import io.sunshower.service.model.Property;
import io.sunshower.service.model.RelationshipType;
import io.sunshower.service.model.properties.IntegerProperty;
import io.sunshower.service.model.properties.StringProperty;
import io.sunshower.service.workspace.model.Workspace;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class OrchestrationTemplateTest extends PersistTestCase {
   
    @PersistenceContext
    private EntityManager entityManager;

    
    @Test
    public void ensureOrchestrationTemplateCanBeSavedWithProperties() {

        final Workspace workspace = new Workspace();
        workspace.setKey("workspace");
        workspace.setName("frapper");
        final OrchestrationTemplate template = new OrchestrationTemplate();
        template.setName("name");
        template.setKey("key");
        workspace.addOrchestrationTemplate(template);
        template.addProperty(new StringProperty("prop", "prop", "cool"));
        template.addProperty(new IntegerProperty("prop", "prop", 100L));
        entityManager.persist(workspace);
        entityManager.flush();

        List<Property<?, ?>> properties = entityManager.find(
                OrchestrationTemplate.class, 
                template.getId()
        ).getProperties();
        
        assertThat(properties.size(), is(2));

    }
    
    
    @Test
    public void ensureOrchestrationTemplateCanBeSavedWithLink() {
        final Workspace workspace = new Workspace();
        workspace.setKey("workspace");
        workspace.setName("frapper");
        final OrchestrationTemplate template = new OrchestrationTemplate();
        template.setName("name");
        template.setKey("key");
        workspace.addOrchestrationTemplate(template);


        final OrchestrationTemplate link = new OrchestrationTemplate();
        link.setName("name2");
        link.setKey("key2");
        link.setWorkspace(workspace);

        workspace.addOrchestrationTemplate(link);

        entityManager.persist(workspace);
        entityManager.flush();

        final TemplateLink linkage = new TemplateLink(
                template,
                link,
                LinkageMode.Linked,
                RelationshipType.Parent
        );
        entityManager.persist(linkage);
        entityManager.flush();
       
        
        TemplateLink l = entityManager.find(TemplateLink.class, linkage.getId());
        assertThat(l.getSource(), is(template));
        assertThat(l.getTarget(), is(link));
    }

}