package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;
import io.sunshower.model.core.io.File;
import io.sunshower.persistence.core.converters.LocaleConverter;
import java.util.Date;
import java.util.Locale;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "USER_DETAILS", schema = Schemata.SUNSHOWER)
public class Details extends ImageAware {

  @Column(name = "locale")
  @Convert(converter = LocaleConverter.class)
  private Locale locale;

  @Basic
  @Size(min = 3, max = 255)
  private String firstname;

  @Basic
  @Size(min = 3, max = 255)
  private String lastname;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "active_until")
  private Date activeUntil;

  @Column(unique = true, name = "phone_number")
  private String phoneNumber;

  @Basic
  @Column(unique = true, name = "email_address")
  private String emailAddress;

  @Temporal(TemporalType.TIMESTAMP)
  private Date registered;

  @Column(name = "last_active")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastActive;

  @OneToOne(mappedBy = "details")
  @JoinColumn(name = "user_id")
  private User user;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "root_id", insertable = false, updatable = false)
  private File root;

  public Details() {}

  public Details(User user) {
    this.user = user;
  }

  public File getRoot() {
    return root;
  }

  public void setRoot(File root) {
    this.root = root;
  }

  public Date getActiveUntil() {
    return activeUntil;
  }

  public void setActiveUntil(Date activeUntil) {
    this.activeUntil = activeUntil;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Date getRegistered() {
    return registered;
  }

  public void setRegistered(Date registered) {
    this.registered = registered;
  }

  public Date getLastActive() {
    return lastActive;
  }

  public void setLastActive(Date lastActive) {
    this.lastActive = lastActive;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
