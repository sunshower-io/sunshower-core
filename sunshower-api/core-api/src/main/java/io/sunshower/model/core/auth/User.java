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
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Entity
@ToString
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.NONE)
@Table(name = "PRINCIPAL", schema = Schemata.SUNSHOWER)
public class User extends ProtectedDistributableEntity
    implements UserDetails, TenantAware, Configurable {

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

  @NotNull
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private Details details;

  @Embedded protected UserConfiguration configuration;

  public User() {
    setId(DistributableEntity.sequence.next());
  }

  public User(User copy) {
    this(copy.getId(), copy.getUsername(), null);
  }

  public User(Identifier id, final String username, final String password) {
    setId(id);
    this.username = username;
    this.password = password;
  }

  public User(Identifier uuid) {
    setId(uuid);
  }

  public void setVisibility(Visibility visibility) {
    super.setVisibility(visibility);
    getDetails().setVisibility(visibility);
  }

  public Configuration getConfiguration() {
    if (configuration == null) {
      configuration = new UserConfiguration();
    }
    return configuration;
  }

  public void setConfiguration(Configuration cfg) {
    this.configuration = (UserConfiguration) cfg;
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
    this.setVisibility(Visibility.Public);
  }

  public User addRole(Role role) {
    role.addUser(this);
    if (this.roles == null) {
      this.roles = new LinkedHashSet<>();
    }
    this.roles.add(role);
    return this;
  }

  public void setAuthorities(Collection<Role> roles) {
    clearRoles();
    if (roles != null) {
      for (Role r : roles) {
        addRole(r);
      }
    }
  }

  public void setRoles(Set<Role> roles) {
    setAuthorities(roles);
  }

  public void removeRoles(Collection<Role> roles) {
    for (Role role : roles) {
      removeRole(role);
    }
  }

  public void clearRoles() {
    if (roles != null) {
      Collection<Role> copy = new HashSet<>(roles);
      for (Role r : copy) {
        removeRole(r);
      }
    }
  }

  public void removeRole(Role role) {
    if (this.roles != null) {
      role.removeUser(this);
      this.roles.remove(role);
    }
  }
}
