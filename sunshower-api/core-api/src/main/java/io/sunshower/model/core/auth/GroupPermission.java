package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;

/** Created by haswell on 5/9/17. */
@Entity
@Table(name = "group_authorities")
public class GroupPermission extends DistributableEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id")
  private Group group;

  @Basic private String authority;

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public String getAuthority() {
    return authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
  }
}
