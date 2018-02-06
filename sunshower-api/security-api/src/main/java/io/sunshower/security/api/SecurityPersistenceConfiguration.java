package io.sunshower.security.api;

import io.sunshower.persistence.annotations.Persistence;
import org.springframework.context.annotation.Configuration;

@Persistence(
  id = "audit",
  scannedPackages = "io.sunshower.service.signup",
  migrationLocations = "classpath:{dialect}"
)
@Configuration
public class SecurityPersistenceConfiguration {}
