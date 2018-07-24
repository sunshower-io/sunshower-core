package io.sunshower.security.api;

import io.sunshower.persistence.annotations.Persistence;
import io.sunshower.service.signup.Products;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Persistence(
  id = "audit",
  migrationLocations = "classpath:{dialect}",
  scannedPackages = {"io.sunshower.service.signup", "io.sunshower.core.security"}
)
@Configuration
public class SecurityPersistenceConfiguration {

  @Bean
  public ProductService productsService() {
    return new Products();
  }
}
