package io.sunshower.service.workspace.model;

import io.sunshower.model.core.BaseModelObject;
import io.sunshower.model.core.Schemata;
import io.sunshower.service.orchestration.model.Template;
import io.sunshower.service.revision.model.Repository;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Cacheable
@Table(name = "WORKSPACE", schema = Schemata.SUNSHOWER)
public class Workspace extends BaseModelObject {

  @Basic
  @NotNull
  @Column(name = "key", unique = true)
  @Size(min = 3, max = 255)
  private String key;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "repository_id")
  private Repository repository;

  @Enumerated private WorkspaceClassification classification;

  @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Template> templates;

  {
    classification = WorkspaceClassification.Project;
  }

  public void addTemplate(Template template) {
    if (templates == null) {
      templates = new HashSet<>();
    }
    template.setWorkspace(this);
    templates.add(template);
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  public Set<Template> getTemplates() {
    return templates;
  }

  public void setTemplates(Set<Template> templates) {
    this.templates = templates;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return "Workspace{" + "key='" + key + '\'' + '}';
  }
}
