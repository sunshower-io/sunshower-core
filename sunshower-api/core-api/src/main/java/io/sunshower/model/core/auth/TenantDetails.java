package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;
import io.sunshower.model.core.io.File;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "TENANT_DETAILS", schema = Schemata.SUNSHOWER)
public class TenantDetails extends ImageAware {

  @OneToOne
  @JoinColumn(name = "tenant_id")
  private Tenant tenant;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "root_id", insertable = false, updatable = false)
  private File root;
}
