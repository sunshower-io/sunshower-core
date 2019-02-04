package io.sunshower.model.core.auth;

import javax.persistence.*;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AssociationOverride(
  name = "properties",
  joinTable =
      @JoinTable(
        name = "USER_TO_USER_CONFIGURATION",
        joinColumns = @JoinColumn(name = "owner_id", referencedColumnName = "id")
      )
)
public class UserConfiguration extends Configuration {}
