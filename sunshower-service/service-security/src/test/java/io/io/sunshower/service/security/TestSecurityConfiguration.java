package io.io.sunshower.service.security;

import static org.mockito.Mockito.mock;

import io.sunshower.core.security.CredentialService;
import io.sunshower.persistence.Dialect;
import io.sunshower.persistence.annotations.Persistence;
import io.sunshower.service.ext.IconService;
import io.sunshower.service.security.PermissionsService;
import io.sunshower.service.security.SpringPermissionsService;
import io.sunshower.service.security.crypto.MessageAuthenticationCode;
import io.sunshower.test.common.TestClasspath;
import io.sunshower.test.common.TestConfigurations;
import io.sunshower.test.persist.ConnectionDetectingJDBCTemplate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import javax.sql.DataSource;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Persistence(
  id = "audit",
  migrationLocations = "classpath:{dialect}",
  scannedPackages = {
    "io.sunshower.model.core",
    "io.sunshower.model.core.io",
    "io.sunshower.service.model",
    "io.sunshower.service.signup",
    "io.sunshower.model.core.auth",
    "io.sunshower.service.model.compute",
    "io.sunshower.service.model.application",
    "io.sunshower.service.model.provider",
    "io.sunshower.service.model.storage",
    "io.sunshower.service.model.workspace",
  }
)
public class TestSecurityConfiguration {
  @Bean
  public IconService iconService() {
    return mock(IconService.class);
  }

  @Bean
  @Primary
  public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
    return new ConnectionDetectingJDBCTemplate(dataSource);
  }

  @Bean
  public PermissionsService<?> permissionsService() {
    return new SpringPermissionsService();
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
  public ExecutorService executorService() {
    return new ForkJoinPool();
  }

  @Bean(name = "caches:authentication")
  public Cache authenticationCache() {
    return mock(Cache.class);
  }

  @Bean
  @Primary
  public Dialect databaseDialect() {
    return Dialect.Postgres;
  }

  @Bean
  @Primary
  public MessageAuthenticationCode messageAuthenticationCode() {
    return new MessageAuthenticationCode(MessageAuthenticationCode.Algorithm.MD5, "frapper");
  }

  @Bean
  @Primary
  public TextEncryptor textEncryptor() {
    final BasicTextEncryptor encryptor = new BasicTextEncryptor();
    encryptor.setPassword("Frap");
    return encryptor;
  }

  @Bean
  public CredentialService credentialService() {
    return new TestCredentialService();
  }

  @Bean
  public TestSecureService testService() {
    return new TestSecureService();
  }
}
