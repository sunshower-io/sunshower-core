package io.sunshower.service.orchestration.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import io.sunshower.service.PersistTestCase;
import io.sunshower.service.model.LinkageMode;
import io.sunshower.service.model.Property;
import io.sunshower.service.model.RelationshipType;
import io.sunshower.service.workspace.model.Workspace;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

public class TemplateTest extends PersistTestCase {

  @PersistenceContext private EntityManager entityManager;

  @Test
  public void ensureTemplateCanBeSavedWithProperties() {

    final Workspace workspace = new Workspace();
    workspace.setKey("workspace");
    workspace.setName("frapper");
    final Template template = new Template();
    template.setName("name");
    template.setKey("key");
    workspace.addTemplate(template);
    template.addProperty(Property.string("prop", "prop", "cool"));
    template.addProperty(Property.integer("prop2", "prop", "100"));
    entityManager.persist(workspace);
    entityManager.flush();

    List<Property> properties =
        entityManager.find(Template.class, template.getId()).getProperties();

    assertThat(properties.size(), is(2));
  }

  @Test
  public void ensureTemplateCanBeSavedWithLink() {
    final Workspace workspace = new Workspace();
    workspace.setKey("workspace");
    workspace.setName("frapper");
    final Template template = new Template();
    template.setName("name");
    template.setKey("key");
    workspace.addTemplate(template);

    final Template link = new Template();
    link.setName("name2");
    link.setKey("key2");
    link.setWorkspace(workspace);

    workspace.addTemplate(link);

    entityManager.persist(workspace);
    entityManager.flush();

    final TemplateLink linkage =
        new TemplateLink(template, link, LinkageMode.Linked, RelationshipType.Parent);
    entityManager.persist(linkage);
    entityManager.flush();

    TemplateLink l = entityManager.find(TemplateLink.class, linkage.getId());
    assertThat(l.getSource(), is(template));
    assertThat(l.getTarget(), is(link));
  }
}
