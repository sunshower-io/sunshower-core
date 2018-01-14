package io.sunshower.model.core.auth;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.Schemata;
import io.sunshower.persistence.core.DistributableEntity;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Entity
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.NONE)
@Table(name = "PRINCIPAL", schema = Schemata.SUNSHOWER)
public class User extends DistributableEntity implements UserDetails, TenantAware {

  @NotNull
  @OneToOne(
    mappedBy = "user",
    cascade = CascadeType.ALL,
    fetch = FetchType.EAGER,
    orphanRemoval = true
  )
  private Details details;

  @Basic
  @XmlAttribute
  @Column(unique = true)
  private String username;

  @Basic
  @XmlAttribute
  @Size(min = 5, max = 255)
  private String password;

  @Basic @XmlAttribute private boolean active;

  @XmlElement(name = "role")
  @XmlElementWrapper(name = "roles")
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
    name = "users_to_roles",
    schema = Schemata.SUNSHOWER,
    joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
  )
  private Set<Role> roles;

  @Getter
  @ManyToOne
  @JoinColumn(name = "tenant_id")
  private Tenant tenant;

  public User() {
    super(DistributableEntity.sequence.next());
  }

  public User(User copy) {
    this(copy.getId(), copy.getUsername(), null);
  }

  public User(Identifier id, final String username, final String password) {
    super(id);
    this.username = username;
    this.password = password;
  }

  public User(Identifier uuid) {
    super(uuid);
  }

  public Set<Role> getRoles() {
    if (roles == null) {
      return Collections.emptySet();
    }
    return roles;
  }

  @Override
  public boolean isAccountNonExpired() {
    return active;
  }

  @Override
  public boolean isAccountNonLocked() {
    return active;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return active;
  }

  @Override
  public boolean isEnabled() {
    return active;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles;
  }

  @Override
  protected void setDefaults() {
    this.setDetails(new Details(this));
  }

  @Override
  public String toString() {
    return "User{"
        + ", details="
        + details
        + ", username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + '}';
  }

  public User addRole(Role role) {
    role.addUser(this);
    if (this.roles == null) {
      this.roles = new LinkedHashSet<>();
    }
    this.roles.add(role);
    return this;
  }

  public void removeRole(Role role) {
    if (this.roles != null) {
      role.removeUser(this);
      this.roles.remove(role);
    }
  }
}
