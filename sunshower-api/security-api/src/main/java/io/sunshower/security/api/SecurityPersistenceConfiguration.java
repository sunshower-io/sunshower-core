package io.sunshower.security.api;

import io.sunshower.persistence.annotations.Persistence;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haswell on 11/17/16.
 */
@Persistence(
    id = "audit",
    scannedPackages = "io.sunshower.service.signup",
    migrationLocations = "classpath:{dialect}"
)
@Configuration
public class SecurityPersistenceConfiguration {

}
