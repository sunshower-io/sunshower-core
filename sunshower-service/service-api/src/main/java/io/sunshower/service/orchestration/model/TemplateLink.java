package io.sunshower.service.orchestration.model;

import io.sunshower.model.core.Schemata;
import io.sunshower.service.model.AbstractEntityLink;
import io.sunshower.service.model.LinkageMode;
import io.sunshower.service.model.RelationshipType;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TEMPLATE_LINK", schema = Schemata.SUNSHOWER)
public class TemplateLink extends AbstractEntityLink<Template, Template> {

  protected TemplateLink() {
    this(null, null, null, null);
  }

  public TemplateLink(Template source, Template target, LinkageMode mode, RelationshipType type) {
    super(source, target, mode, type);
    if (target != null) {
      target.setLink(this);
    }
  }

  public TemplateLink(Template template, Template linked) {

    this(template, linked, LinkageMode.Linked, RelationshipType.Parent);
  }
}
