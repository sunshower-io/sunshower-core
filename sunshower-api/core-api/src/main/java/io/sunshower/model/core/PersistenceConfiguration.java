package io.sunshower.model.core;

import io.sunshower.persistence.annotations.Persistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haswell on 11/16/16.
 */
@Configuration
@Persistence(
        id = "audit",
        migrationLocations = "classpath:{dialect}",
        scannedPackages = {
                "io.sunshower.service.signup",
                "io.sunshower.model.core.auth",
                "io.sunshower.model.core",
                "io.sunshower.model.core.deployment",
                "io.sunshower.model.core.configuration"
        })
public class PersistenceConfiguration {


}
