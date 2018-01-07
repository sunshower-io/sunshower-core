package io.sunshower.service.orchestration.model;

import io.sunshower.model.core.Version;
import io.sunshower.service.model.BaseModelObject;
import io.sunshower.service.model.Link;
import io.sunshower.service.model.Linked;
import io.sunshower.service.model.PropertyAwareObject;
import io.sunshower.service.workspace.model.Workspace;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by haswell on 5/16/17.
 */
@Entity
@Table(name = "ORCHESTRATION_TEMPLATE")
public class OrchestrationTemplate 
        extends PropertyAwareObject<OrchestrationTemplate>
        implements Linked<
            OrchestrationTemplate, 
            OrchestrationTemplate
        > {

    @Basic
    @NotNull
    @Column(
            name = "key",
            unique = true
    )
    @Size(min = 3, max = 255)
    private String key;

    @Basic
    @Column(name = "description")
    @Size(max = 255)
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    private Version version;


    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;


    @OneToOne
    private TemplateLink link;

    public OrchestrationTemplate() {
        super(OrchestrationTemplate.class);
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "OrchestrationTemplate{" +
                "key='" + key + '\'' +
                "description='" + description + '\'' +
                '}';
    }

    public Link<OrchestrationTemplate, OrchestrationTemplate> getLink() {
        return link;
    }

    protected void setLink(TemplateLink link) {
        this.link = link;
    }

    
}