package io.sunshower.model.core.auth;

import javax.persistence.AssociationOverride;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AssociationOverride(
  name = "properties",
  joinTable =
      @JoinTable(
        name = "TENANT_TO_TENANT_CONFIGURATION",
        joinColumns = @JoinColumn(name = "owner_id", referencedColumnName = "id")
      )
)
public class TenantConfiguration extends Configuration {}
