package io.sunshower.model.core;

import io.sunshower.persistence.annotations.Persistence;
import org.springframework.context.annotation.Configuration;

@Configuration
@Persistence(
  id = "audit",
  schema = "SUNSHOWER",
  migrationLocations = "classpath:{dialect}",
  scannedPackages = {
    "io.sunshower.service.signup",
    "io.sunshower.model.core.auth",
    "io.sunshower.model.core",
    "io.sunshower.core.security",
    "io.sunshower.model.core.deployment",
    "io.sunshower.model.core.configuration"
  }
)
public class PersistenceConfiguration {}
