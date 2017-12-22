package io.sunshower.service.orchestration.model;

import io.sunshower.service.model.AbstractEntityLink;
import io.sunshower.service.model.LinkageMode;
import io.sunshower.service.model.RelationshipType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "ORCHESTRATION_TEMPLATE_LINK")
public class TemplateLink extends AbstractEntityLink<OrchestrationTemplate, OrchestrationTemplate> {

    protected TemplateLink() {
        this(null, null, null, null);
    }


    public TemplateLink(
            OrchestrationTemplate source,
            OrchestrationTemplate target,
            LinkageMode mode,
            RelationshipType type
    ) {
        super(source, target, mode, type);
        if(target != null) {
            target.setLink(this);
        }
    }

    public TemplateLink(
            OrchestrationTemplate template,
            OrchestrationTemplate linked
    ) {

        this(
                template,
                linked,
                LinkageMode.Linked,
                RelationshipType.Parent
        );
    }
}
