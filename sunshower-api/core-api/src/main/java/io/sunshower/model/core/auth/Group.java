package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/** Created by haswell on 5/9/17. */
@Entity
@Table(name = "GROUPS")
public class Group extends DistributableEntity {

  @Column(name = "group_name")
  private String name;

  @ManyToMany(targetEntity = User.class)
  @JoinTable(
    name = "group_members",
    joinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id")},
    inverseJoinColumns = @JoinColumn(name = "username", referencedColumnName = "username")
  )
  private Set<User> members;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<User> getMembers() {
    return members;
  }

  public void setMembers(Set<User> members) {
    this.members = members;
  }

  public void addMember(User member) {
    if (member == null) {
      members = new HashSet<>();
    }
    members.add(member);
  }
}
