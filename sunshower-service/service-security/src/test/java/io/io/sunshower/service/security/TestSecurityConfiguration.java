package io.io.sunshower.service.security;

import io.sunshower.core.security.CredentialService;
import io.sunshower.persistence.Dialect;
import io.sunshower.persistence.annotations.Persistence;
import io.sunshower.service.application.DefaultApplicationService;
import io.sunshower.service.security.ApplicationService;
import io.sunshower.service.security.crypto.MessageAuthenticationCode;
import io.sunshower.test.common.TestConfigurations;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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

  @Primary
  @Bean(name = TestConfigurations.TEST_CONFIGURATION_REPOSITORY_PATH)
  public String location() {
    return "/sunshower-api/core-api/src/test/resources";
  }

  @Bean
  public ApplicationService applicationService() {
    return new DefaultApplicationService();
  }

  @Bean
  public ExecutorService executorService() {
    return new ForkJoinPool();
  }

  @Bean(name = "caches:authentication")
  public Cache authenticationCache() {
    return Mockito.mock(Cache.class);
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
