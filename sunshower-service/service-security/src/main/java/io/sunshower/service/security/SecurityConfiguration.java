package io.sunshower.service.security;

import io.sunshower.common.rs.ClassParameterProviderFactory;
import io.sunshower.core.security.AuthenticationService;
import io.sunshower.core.security.RoleService;
import io.sunshower.core.security.UserService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.vault.KeyProvider;
import io.sunshower.security.api.SecurityPersistenceConfiguration;
import io.sunshower.service.application.DefaultApplicationService;
import io.sunshower.service.security.crypto.InstanceSecureKeyGenerator;
import io.sunshower.service.security.crypto.MessageAuthenticationCode;
import io.sunshower.service.security.crypto.StrongEncryptionService;
import io.sunshower.service.security.jaxrs.AuthenticationContextProvider;
import io.sunshower.service.security.user.DefaultUserService;
import io.sunshower.service.signup.SignupService;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableCaching
@Import(SecurityPersistenceConfiguration.class)
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Inject private UserService userService;
  static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

  @Bean
  public ApplicationService applicationService() {
    return new DefaultApplicationService();
  }

  @Bean
  public TokenManager tokenManager() {
    return new GridTokenManager();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    logger.info("disabling web security in favor of method security");
    http.anonymous().configure(http);
    http.authorizeRequests().anyRequest().permitAll();
  }

  @Bean
  public Session userFacade(UserService userService) {
    return new AuthenticationSession(userService);
  }

  @Bean
  public ClassParameterProviderFactory classParameterProviderFactory() {
    return new ClassParameterProviderFactory();
  }

  @Bean
  public AuthenticationContextProvider authenticationProvider() {
    return new AuthenticationContextProvider();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService((UserDetailsService) userService);
  }

  @Bean(name = "caches:spring:acl")
  public Cache springAclCache(CacheManager cacheManager) {
    return cacheManager.getCache("caches:spring:acl");
  }

  @Bean
  public UserService userService() {
    return new DefaultUserService();
  }

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter();
  }

  @Bean
  public RoleService roleService() {
    return new DefaultRoleService();
  }

  @Bean
  public KeyProvider keyProvider() {
    return new InstanceSecureKeyGenerator();
  }

  @Bean
  @Singleton
  public MessageAuthenticationCode messageAuthenticationCode(KeyProvider keyProvider) {
    return new MessageAuthenticationCode(
        MessageAuthenticationCode.Algorithm.SHA256, keyProvider.getKey());
  }

  @Bean
  public SignupService signupService() {
    return new DefaultSignupService();
  }

  @Bean
  public EncryptionService encryptionService() {
    return new StrongEncryptionService();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationService authenticationService() {
    return new DefaultAuthenticationService();
  }

  @Bean
  public TextEncryptor textEncryptor(KeyProvider keyProvider) {
    final StrongTextEncryptor result = new StrongTextEncryptor();
    result.setPassword(keyProvider.getKey());
    return result;
  }

  @Bean
  public MutableAclService jdbcAclService(
      JdbcTemplate template, LookupStrategy lookupStrategy, AclCache aclCache) {
    return new IdentifierJdbcMutableAclService(template, lookupStrategy, aclCache, "SUNSHOWER");
  }

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return new CachingRoleHierarchy();
  }

  @Bean
  public PermissionEvaluator permissionEvaluator(AclService aclService) {
    return new MultitenantedHierarchicalPermissionEvaluator(aclService);
  }

  @Bean
  public SpringCacheManager springCacheManager() {
    final SpringCacheManager springCacheManager = new SpringCacheManager();
    springCacheManager.setIgniteInstanceName("sunshower-data-fabric");
    return springCacheManager;
  }

  @Bean
  public AclCache aclCache(
      @Named("caches:spring:acl") Cache cache,
      PermissionGrantingStrategy permissionGrantingStrategy,
      AclAuthorizationStrategy aclAuthorizationStrategy) {
    return new SpringCacheBasedAclCache(
        cache, permissionGrantingStrategy, aclAuthorizationStrategy);
  }

  @Bean
  public LookupStrategy aclLookupStrategy(
      DataSource dataSource,
      AclCache aclCache,
      AclAuthorizationStrategy aclAuthorizationStrategy,
      PermissionGrantingStrategy permissionGrantingStrategy) {
    return new IdentifierEnabledLookupStrategy(
        "SUNSHOWER", dataSource, aclCache, aclAuthorizationStrategy, permissionGrantingStrategy);
  }

  @Bean
  public static GrantedAuthority administratorRole() {
    return DefaultRoles.SITE_ADMINISTRATOR.toRole();
  }

  @Bean
  public AclAuthorizationStrategy aclAuthorizationStrategy(GrantedAuthority role) {
    return new MultitenantedAclAuthorizationStrategy(role);
  }

  @Bean
  public PermissionGrantingStrategy permissionGrantingStrategy(AuditLogger logger) {
    return new DefaultPermissionGrantingStrategy(logger);
  }

  @Bean
  public AuditLogger securityAuditLogger() {
    return new ConsoleAuditLogger();
  }
}
