package io.sunshower.service.cfg;

import io.sunshower.persistence.annotations.Persistence;
import org.springframework.context.annotation.Configuration;

@Configuration
@Persistence(
  id = "audit",
  migrationLocations = "classpath:{dialect}",
  scannedPackages = {
    "io.sunshower.model.core.security",
    "io.sunshower.service.revision.model",
    "io.sunshower.service.workspace.model",
    "io.sunshower.service.orchestration.model",
    "io.sunshower.service.model.properties"
  }
)
public class ServicePersistenceConfiguration {}
