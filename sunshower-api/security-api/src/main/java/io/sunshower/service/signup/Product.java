package io.sunshower.service.signup;

import io.sunshower.model.core.Schemata;
import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PRODUCT", schema = Schemata.SUNSHOWER)
public class Product extends DistributableEntity {

  @Basic private String name;

  @Basic private String description;

  @ManyToOne
  @JoinTable(
    name = "REQUEST_TO_PRODUCT",
    schema = Schemata.SUNSHOWER,
    joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id")
  )
  private RegistrationRequest request;

  public Product(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
