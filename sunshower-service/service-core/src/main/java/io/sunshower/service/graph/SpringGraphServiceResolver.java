package io.sunshower.service.graph;

import io.sunshower.service.graph.service.GraphService;
import io.sunshower.service.graph.service.GraphServiceResolver;
import org.springframework.context.ApplicationContext;

public class SpringGraphServiceResolver implements GraphServiceResolver {

  private final ApplicationContext context;

  public SpringGraphServiceResolver(final ApplicationContext context) {
    this.context = context;
  }

  @Override
  public <T extends GraphService> T resolve(Class<T> type, String name) {
    return this.context.getBean(name, type);
  }
}
