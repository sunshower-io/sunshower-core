package io.sunshower.service;

import io.sunshower.persistence.Dialect;
import io.sunshower.persistence.annotations.CacheMode;
import io.sunshower.service.git.MockRepositoryResolutionStrategy;
import io.sunshower.service.model.io.FileResolutionStrategy;
import io.sunshower.service.security.AuthenticationSession;
import io.sunshower.service.security.Session;
import io.sunshower.service.serialization.DynamicJaxrsProviders;
import io.sunshower.service.serialization.DynamicResolvingMoxyJsonProvider;
import io.sunshower.test.common.TestClasspath;
import io.sunshower.test.common.TestConfigurations;
import io.sunshower.test.persist.ConnectionDetectingJDBCTemplate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
@Configuration
@CacheMode(CacheMode.Mode.Local)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TestConfiguration {

  @Bean
  @Primary
  public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
    return new ConnectionDetectingJDBCTemplate(dataSource);
  }

  @Primary
  @Bean(name = TestConfigurations.TEST_CONFIGURATION_REPOSITORY_PATH)
  public String location() {
    return TestClasspath.rootDir()
        .getParent()
        .getParent()
        .resolve("sunshower-api/core-api/src/test/resources")
        .toFile()
        .getAbsolutePath();
  }

  @Bean
  @Primary
  public FileResolutionStrategy defaultFileResolutionStrategy() {
    return new MockRepositoryResolutionStrategy();
  }

  @Bean
  public DynamicResolvingMoxyJsonProvider moXyJsonProvider(DynamicJaxrsProviders providers) {
    return new DynamicResolvingMoxyJsonProvider(providers);
  }

  @Bean
  public Dialect databaseDialect() {
    return Dialect.Postgres;
  }

  @Bean
  public ExecutorService executorService() {
    return new ForkJoinPool();
  }

  @Bean
  public Session userFacade() {
    return new AuthenticationSession();
  }
}
