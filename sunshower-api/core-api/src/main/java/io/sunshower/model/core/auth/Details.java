package io.sunshower.model.core.auth;

import io.sunshower.model.core.ImageAware;
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

  @Column(name = "login_count")
  private Integer loginCount;

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

  public Details() {
    super();
  }

  public Details(User user) {
    this.user = user;
  }

  @Override
  @PrePersist
  protected void setDefaults() {
    super.setDefaults();
    if (loginCount == null) {
      loginCount = 0;
    }
  }

  public String getFirstName() {
    return getName();
  }

  public void setFirstName(String name) {
    setName(name);
  }
}
