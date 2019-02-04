package io.sunshower.model.core.auth;

import io.sunshower.common.Identifier;
import io.sunshower.persistence.core.DistributableHierarchicalEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@MappedSuperclass
public abstract class ProtectedDistributableHierarchicalEntity<
        T extends ProtectedDistributableHierarchicalEntity<T>>
    extends DistributableHierarchicalEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id", insertable = false, updatable = false)
  private ObjectIdentity identity;

  protected ProtectedDistributableHierarchicalEntity(Identifier id) {
    super(id);
  }
}
