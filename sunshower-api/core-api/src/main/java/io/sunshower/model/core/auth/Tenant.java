package io.sunshower.model.core.auth;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.Schemata;
import io.sunshower.persistence.core.Hierarchical;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "TENANT", schema = Schemata.SUNSHOWER)
public class Tenant extends ProtectedDistributableEntity
    implements Hierarchical<Identifier, Tenant>, Configurable {

  @Basic private String name;

  @ManyToOne private Tenant parent;

  @OneToOne(mappedBy = "tenant", cascade = CascadeType.ALL)
  private TenantDetails details;

  @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<User> users;

  @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Set<Tenant> children;

  @Embedded private TenantConfiguration configuration;

  public Tenant() {
    super();
    setDefaults();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Tenant getParent() {
    return parent;
  }

  @Override
  public Set<Tenant> getChildren() {
    return children;
  }

  @Override
  public boolean addChild(Tenant tenant) {
    setParent(tenant);
    if (tenant != null) {
      return children().add(tenant);
    }
    return false;
  }

  @Override
  public void setParent(Tenant tenant) {
    this.parent = tenant;
  }

  public TenantDetails getDetails() {
    return details;
  }

  public void setDetails(TenantDetails details) {
    this.details = details;
    if (details != null) {
      details.setTenant(this);
    }
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

  public void addUser(User user) {
    if (users == null) {
      users = new HashSet<>();
    }
    users.add(user);
    user.setTenant(this);
  }

  protected Set<Tenant> children() {
    if (children == null) {
      children = new HashSet<>();
    }
    return children;
  }

  public void setVisibility(Visibility visibility) {
    super.setVisibility(visibility);
    getDetails().setVisibility(Visibility.Public);
  }

  @Override
  protected void setDefaults() {
    setDetails(new TenantDetails());
    setVisibility(Visibility.Public);
  }

  @Override
  public Configuration getConfiguration() {
    if (configuration == null) {
      configuration = new TenantConfiguration();
    }
    return configuration;
  }

  @Override
  public void setConfiguration(Configuration cfg) {}
}
