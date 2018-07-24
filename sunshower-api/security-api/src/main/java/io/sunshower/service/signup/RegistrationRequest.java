package io.sunshower.service.signup;

import io.sunshower.common.crypto.Hashes;
import io.sunshower.common.crypto.Multihash;
import io.sunshower.model.core.Schemata;
import io.sunshower.model.core.auth.User;
import io.sunshower.persistence.core.DistributableEntity;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Entity
@XmlRootElement(name = "request")
@Table(name = "REGISTRATION_REQUEST", schema = Schemata.SUNSHOWER)
public class RegistrationRequest extends DistributableEntity {

  @XmlElement
  @Column(name = "request_id")
  private String requestId;

  @OneToOne(cascade = CascadeType.PERSIST)
  @XmlElement
  @JoinColumn(name = "USER_ID")
  private User user;

  @XmlAttribute
  @Temporal(TemporalType.DATE)
  private Date requested;

  @XmlAttribute
  @Temporal(TemporalType.DATE)
  private Date expires;

  @Getter
  @Setter
  @OneToMany(mappedBy = "request", cascade = CascadeType.PERSIST)
  private Set<Product> products;

  public RegistrationRequest() {}

  public RegistrationRequest(User user) {
    this.user = user;
    this.setDefaults();
  }

  public void addProduct(Product p) {
    if (products == null) {
      products = new HashSet<>();
    }
    products.add(p);
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Date getRequested() {
    return requested;
  }

  public void setRequested(Date requested) {
    this.requested = requested;
  }

  public Date getExpires() {
    return expires;
  }

  public void setExpires(Date expires) {
    this.expires = expires;
  }

  public String getRequestId() {
    return requestId;
  }

  @Override
  protected void setDefaults() {
    this.requested = new Date();
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(requested);
    calendar.add(Calendar.DATE, 1);
    this.expires = calendar.getTime();
    this.requestId = Hashes.create(Multihash.Type.SHA_2_512).hash(user, requested, expires);
  }
}
