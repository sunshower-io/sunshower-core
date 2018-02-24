package io.sunshower.service;

import io.sunshower.persistence.annotations.Persistence;
import io.sunshower.service.application.DefaultApplicationService;
import io.sunshower.service.git.JGitRepositoryService;
import io.sunshower.service.git.RepositoryService;
import io.sunshower.service.graph.SpringGraphServiceResolver;
import io.sunshower.service.graph.service.GraphServiceResolver;
import io.sunshower.service.graph.service.TaskService;
import io.sunshower.service.io.DefaultConfigurableFileResolutionStrategy;
import io.sunshower.service.model.io.FileResolutionStrategy;
import io.sunshower.service.orchestration.JpaTemplateService;
import io.sunshower.service.orchestration.service.TemplateService;
import io.sunshower.service.security.*;
import io.sunshower.service.serialization.DynamicJaxrsProviders;
import io.sunshower.service.serialization.DynamicResolvingMoxyJsonProvider;
import io.sunshower.service.serialization.MOXyJsonGraphContext;
import io.sunshower.service.task.ElementContext;
import io.sunshower.service.task.exec.*;
import io.sunshower.service.tasks.EntityResolverTask;
import io.sunshower.service.workspace.JpaWorkspaceService;
import io.sunshower.service.workspace.service.WorkspaceService;
import java.util.concurrent.ExecutorService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Persistence(
  id = "audit",
  schema = "SUNSHOWER",
  migrationLocations = "classpath:{dialect}",
  scannedPackages = {
    "io.sunshower.model.core",
    "io.sunshower.service.model",
    "io.sunshower.service.signup",
    "io.sunshower.model.core.auth",
    "io.sunshower.service.revision",
    "io.sunshower.service.model.compute",
    "io.sunshower.service.model.application",
    "io.sunshower.service.model.provider",
    "io.sunshower.service.model.storage",
    "io.sunshower.service.workspace.model",
    "io.sunshower.service.orchestration.model"
  }
)
public class CoreServiceConfiguration {

  @Bean
  public GraphTransformer graphTransformer() {
    return new DefaultGraphTransformer();
  }

  @Bean
  public TaskService taskService() {
    return new DefaultTaskService();
  }

  @Bean
  public CredentialService credentialService() {
    return new JpaCredentialService();
  }

  @Bean
  public FileResolutionStrategy fileResolutionStrategy() {
    return new DefaultConfigurableFileResolutionStrategy();
  }

  @Bean
  public GraphServiceResolver graphServiceResolver(ApplicationContext ctx) {
    return new SpringGraphServiceResolver(ctx);
  }

  @Bean
  public DynamicJaxrsProviders dynamicJaxrsProviders() {
    return new DynamicJaxrsProviders();
  }

  @Bean
  public MOXyJsonGraphContext moXyJsonGraphContext(
      DynamicResolvingMoxyJsonProvider provider, DynamicJaxrsProviders providers) {
    return new MOXyJsonGraphContext(provider, providers);
  }

  @Bean
  public RepositoryService repositoryService() {
    return new JGitRepositoryService();
  }

  @Bean
  public ApplicationService applicationService() {
    return new DefaultApplicationService();
  }

  @Bean
  public SpringPermissionsService springPermissionsService() {
    return new SpringPermissionsService();
  }

  @Bean
  public WorkspaceService workspaceService() {
    return new JpaWorkspaceService();
  }

  @Bean(name = TemplateService.NAME)
  public TemplateService orchestrationTemplateService() {
    return new JpaTemplateService();
  }

  @Bean
  public ParallelTaskExecutor parallelTaskExecutor(
      ElementContext elementContext, ExecutorService executor, ApplicationContext context) {
    return new ParallelTaskExecutor(elementContext, executor, context);
  }

  @Bean
  public ElementContext elementContext() {
    ElementContext context = new SpringElementContext();
    context.register(EntityResolverTask.key, EntityResolverTask.class);
    return context;
  }

  @Bean(name = "caches:authentication")
  public Cache authenticationCache(CacheManager cacheManager) {
    return cacheManager.getCache("caches:authentication");
  }
}
