package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;
import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;

@Entity
@Table(name = "acl_entry", schema = Schemata.SUNSHOWER)
public class PermissionEntry extends DistributableEntity {

  @Basic
  @Column(name = "ace_order")
  private int order;

  @Basic private int mask;

  @Basic private boolean granting;

  @Embedded private AuditStatus status;

  @OneToOne
  @JoinColumn(name = "sid")
  private SecurityIdentity identity;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "acl_object_identity")
  private SecuredObject instance;

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public int getMask() {
    return mask;
  }

  public void setMask(int mask) {
    this.mask = mask;
  }

  public boolean isGranting() {
    return granting;
  }

  public void setGranting(boolean granting) {
    this.granting = granting;
  }

  public AuditStatus getStatus() {
    return status;
  }

  public void setStatus(AuditStatus status) {
    this.status = status;
  }

  public SecurityIdentity getIdentity() {
    return identity;
  }

  public void setIdentity(SecurityIdentity identity) {
    this.identity = identity;
  }

  public SecuredObject getInstance() {
    return instance;
  }

  public void setInstance(SecuredObject instance) {
    this.instance = instance;
  }
}
