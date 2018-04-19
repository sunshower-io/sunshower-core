package io.sunshower.service.model;

import io.sunshower.model.core.auth.ProtectedDistributableEntity;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@MappedSuperclass
@EntityListeners(UpdateListener.class)
public abstract class BaseModelObject extends ProtectedDistributableEntity implements Updatable {

  @Basic
  @Column(name = "name", unique = true)
  @Size(min = 3, max = 255)
  private String name;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  private Date modified;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  @Override
  public void setLastModified(Date date) {
    this.modified = date == null ? new Date() : date;
  }

  @PrePersist
  protected void setDefaults() {
    if (created == null) {
      created = new Date();
    }
    if (modified == null) {
      modified = new Date();
    }
  }
}
