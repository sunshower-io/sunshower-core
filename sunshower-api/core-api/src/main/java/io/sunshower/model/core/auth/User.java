package io.sunshower.model.core.auth;

import io.sunshower.common.Identifier;
import io.sunshower.persistence.core.DistributableEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.util.*;

@Entity
@Table(name = "PRINCIPAL")
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.NONE)
public class User extends DistributableEntity implements UserDetails, TenantAware {



    @NotNull
    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
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

    @Basic
    @XmlAttribute
    private boolean active;

    @XmlElement(name = "role")
    @XmlElementWrapper(name = "roles")
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_to_roles",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            )
    )
    private Set<Role> roles;

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


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles;
    }


    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
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

    public void setEnabled(boolean enabled) {
        this.active = enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    protected void setDefaults() {
        this.setDetails(new Details(this));
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "User{" +
                ", details=" + details +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public User addRole(Role role) {
        role.addUser(this);
        if (this.roles == null) {
            this.roles = new LinkedHashSet<>();
        }
        this.roles.add(role);
        return this;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
