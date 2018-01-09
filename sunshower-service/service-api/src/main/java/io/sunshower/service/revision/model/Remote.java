package io.sunshower.service.revision.model;

import io.sunshower.model.core.auth.Credential;
import io.sunshower.net.validation.Url;
import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/** Created by haswell on 5/22/17. */
@Entity
@Table(name = "GIT_REMOTE")
public class Remote extends DistributableEntity {

  @Basic @NotNull private String name;

  @Url @Basic @NotNull private String uri;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "credential_id")
  private Credential credential;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Credential getCredential() {
    return credential;
  }

  public void setCredential(Credential credential) {
    this.credential = credential;
  }
}
