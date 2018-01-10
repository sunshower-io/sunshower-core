package io.sunshower.model.core.auth;

import io.sunshower.model.core.Application;
import io.sunshower.model.core.auth.User;
import io.sunshower.persistence.core.DistributableEntity;

import javax.persistence.*;
import java.util.Date;

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

  public Activation() {
    super();
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public User getActivator() {
    return activator;
  }

  public void setActivator(User activator) {
    this.activator = activator;
  }

  public Date getActivationDate() {
    return activationDate;
  }

  public void setActivationDate(Date activationDate) {
    this.activationDate = activationDate;
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }
}
