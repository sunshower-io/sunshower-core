package io.sunshower.model.core;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.User;
import io.sunshower.persistence.core.DistributableEntity;
import java.util.*;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
