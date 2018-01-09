package io.sunshower.service.tasks;

import io.sunshower.service.task.Context;
import io.sunshower.service.task.Run;
import io.sunshower.service.task.Task;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

/** Created by haswell on 3/27/17. */
@Task(key = EntityResolverTask.key, definition = EntityResolverNode.class)
@Transactional
public class EntityResolverTask {

  public static final String key = "service::persist::resolve";

  @Context protected EntityResolverNode node;

  @PersistenceContext protected EntityManager entityManager;

  @Run
  public Object resolve() {
    return entityManager.find(node.getType(), node.getId().value());
  }
}
