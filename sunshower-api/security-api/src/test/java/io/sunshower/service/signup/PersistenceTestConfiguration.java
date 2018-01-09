package io.sunshower.service.signup;

import io.sunshower.persistence.Dialect;
import io.sunshower.test.common.TestConfigurations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PersistenceTestConfiguration {

  @Primary
  @Bean(name = TestConfigurations.TEST_CONFIGURATION_REPOSITORY_PATH)
  public String location() {
    return "/sunshower-api/core-api/src/test/resources";
  }

  @Bean
  public Dialect databaseDialect() {
    return Dialect.Postgres;
  }
}
