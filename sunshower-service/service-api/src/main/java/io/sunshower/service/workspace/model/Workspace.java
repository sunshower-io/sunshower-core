package io.sunshower.service.workspace.model;

import io.sunshower.service.model.BaseModelObject;
import io.sunshower.service.orchestration.model.OrchestrationTemplate;
import io.sunshower.service.revision.model.Repository;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by haswell on 5/9/17.
 */
@Entity
@Cacheable
@Table(name = "WORKSPACE")
public class Workspace extends BaseModelObject {
    

    @Basic
    @NotNull
    @Column(
            name = "key",
            unique = true
    )
    @Size(min = 3, max = 255)
    private String key;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "repository_id")
    private Repository repository;
    
    @Enumerated
    private WorkspaceClassification classification;


    @OneToMany(
            mappedBy = "workspace",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<OrchestrationTemplate> orchestrationTemplates;
    
    {
        classification = WorkspaceClassification.Project;
    }


    public void addOrchestrationTemplate(OrchestrationTemplate template) {
        if(orchestrationTemplates == null) {
            orchestrationTemplates = new HashSet<>();
        }
        template.setWorkspace(this);
        orchestrationTemplates.add(template);
    }


    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Set<OrchestrationTemplate> getOrchestrationTemplates() {
        return orchestrationTemplates;
    }

    public void setOrchestrationTemplates(Set<OrchestrationTemplate> orchestrationTemplates) {
        this.orchestrationTemplates = orchestrationTemplates;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Workspace{" +
                "key='" + key + '\'' +
                '}';
    }
}
