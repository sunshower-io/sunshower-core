package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;

@MappedSuperclass
public abstract class ProtectedDistributableEntity extends DistributableEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id", insertable = false, updatable = false)
  private ObjectIdentity identity;

  public ObjectIdentity getIdentity() {
    return identity;
  }

  public void setIdentity(ObjectIdentity identity) {
    this.identity = identity;
  }
}
