package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;
import java.util.*;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.security.core.GrantedAuthority;

/** Created by haswell on 10/20/16. */
@Entity
@XmlRootElement
@Table(name = "ROLE")
public class Role extends DistributableEntity implements GrantedAuthority, Comparable<Role> {

  @Basic @XmlElement private String authority;

  @Basic @XmlElement private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Role parent;

  @OneToMany(
    fetch = FetchType.EAGER,
    mappedBy = "parent",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private Set<Role> children;

  @ManyToMany(mappedBy = "roles")
  private Set<User> users;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
    name = "roles_to_permissions",
    joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
  )
  private Set<Permission> permissions;

  public Role() {
    super();
  }

  public Role(String authority) {
    this(authority, null);
  }

  public Role(String authority, String description) {
    this();
    this.authority = authority;
    this.description = description;
  }

  public Set<Role> getChildren() {
    if (children != null) {
      return Collections.unmodifiableSet(children);
    } else {
      return Collections.emptySet();
    }
  }

  public Role getParent() {
    return parent;
  }

  public Role setParent(Role parent) {
    this.parent = parent;
    return this;
  }

  public Role addChild(Role child) {
    Objects.requireNonNull(child, "Child must not be null");
    if (this.children == null) {
      this.children = new HashSet<>();
    }
    this.children.add(child);
    child.setParent(this);
    return this;
  }

  public Role addPermission(final Permission permission) {
    permission.addRole(this);
    if (this.permissions == null) {
      this.permissions = new LinkedHashSet<>();
    }
    this.permissions.add(permission);
    return this;
  }

  @Override
  public int compareTo(Role o) {
    return 0;
  }

  @Override
  public String getAuthority() {
    return authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
  }

  public void addUser(User user) {
    if (this.users == null) {
      this.users = new LinkedHashSet<>();
    }
    this.users.add(user);
  }

  @Override
  protected void setDefaults() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role)) return false;

    Role role = (Role) o;

    return authority != null ? authority.equals(role.authority) : role.authority == null;
  }

  @Override
  public int hashCode() {
    return authority != null ? authority.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Role{"
        + ", authority='"
        + authority
        + '\''
        + ", description='"
        + description
        + '\''
        + '}';
  }
}
