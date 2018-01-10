package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;
import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;

@Entity
@Table(name = "acl_sid", schema = Schemata.SUNSHOWER)
public class SecurityIdentity extends DistributableEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sid")
  private User owner;

  @Basic
  @Column(name = "sid", insertable = false, updatable = false)
  private String username;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Basic private boolean principal;

  public boolean isPrincipal() {
    return principal;
  }

  public void setPrincipal(boolean principal) {
    this.principal = principal;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }
}
