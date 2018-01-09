package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 10/20/16. */
@Entity
@XmlRootElement
public class Permission extends DistributableEntity {

  @Basic @Column private String name;

  @Basic private String description;

  @ManyToMany(mappedBy = "permissions")
  private Set<Role> roles;

  public Permission() {
    super();
  }

  public Permission(final String name) {
    this();
  }

  public void addRole(Role role) {
    if (this.roles == null) {
      this.roles = new LinkedHashSet<>();
    }
    this.roles.add(role);
  }

  @Override
  public String toString() {
    return "Permission{" + "name='" + name + '\'' + ", description='" + description + '\'' + '}';
  }
}
