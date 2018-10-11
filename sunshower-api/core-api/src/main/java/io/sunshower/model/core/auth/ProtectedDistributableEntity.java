package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class ProtectedDistributableEntity extends DistributableEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id", insertable = false, updatable = false)
  private ObjectIdentity identity;

  @Column @Enumerated private Visibility visibility = Visibility.Private;

  public ProtectedDistributableEntity() {
    super();
  }
}
