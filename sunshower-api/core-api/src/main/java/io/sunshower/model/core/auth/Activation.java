package io.sunshower.model.core.auth;

import io.sunshower.model.core.Application;
import io.sunshower.persistence.core.DistributableEntity;
import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ACTIVATION", schema = "SUNSHOWER")
public class Activation extends DistributableEntity {

  @Basic
  @Column(name = "active")
  private boolean active;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "activator_id")
  private User activator;

  @Column(name = "activation_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date activationDate;

  @JoinColumn(name = "application_id")
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private Application application;
}
