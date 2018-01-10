package io.sunshower.model.core;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.User;
import io.sunshower.persistence.core.DistributableEntity;
import java.util.*;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

/**
 * Application
 *
 * @author haswell
 */
@Entity
@Table(name = "APPLICATION", schema = Schemata.SUNSHOWER)
@XmlRootElement(name = "application")
@IdClass(Identifier.class)
@XmlAccessorType(XmlAccessType.NONE)
public class Application {

  @Id private byte[] id;

  @XmlAttribute private Boolean enabled;

  @XmlElement
  @Column(name = "instance_id")
  private String instanceId;

  @XmlElement private String location;

  @XmlElement
  @Column(name = "`name`")
  private String name;

  @XmlAttribute
  @Column(name = "started_on")
  private Date instanceStarted;

  @XmlElement
  @Column(name = "last_shutdown")
  private Date lastShutdown;

  @XmlElement
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private Version version;

  @Transient @XmlElement private List<User> administrators = new ArrayList<>();

  public Application() {
    this.id = DistributableEntity.sequence.next().value();
  }

  public void addAdministrator(User u) {
    this.administrators.add(u);
  }

  public Identifier getId() {
    return Identifier.valueOf(id);
  }

  public void setId(Identifier id) {
    if (id != null) {
      this.id = id.value();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Date getInstanceStarted() {
    return instanceStarted;
  }

  public void setInstanceStarted(Date instanceStarted) {
    this.instanceStarted = instanceStarted;
  }

  public Date getLastShutdown() {
    return lastShutdown;
  }

  public void setLastShutdown(Date lastShutdown) {
    this.lastShutdown = lastShutdown;
  }

  public Version getVersion() {
    return version;
  }

  public void setVersion(Version version) {
    this.version = version;
  }

  public List<User> getAdministrators() {
    return administrators;
  }

  public void setAdministrators(List<User> administrators) {
    this.administrators = administrators;
  }
}
