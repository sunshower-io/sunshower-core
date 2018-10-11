package io.sunshower.service.orchestration.service;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.sunshower.common.Identifier;
import io.sunshower.model.core.Property;
import io.sunshower.service.graph.service.GraphService;
import io.sunshower.service.hal.core.Graph;
import io.sunshower.service.model.Message;
import io.sunshower.service.orchestration.model.Template;
import io.sunshower.service.orchestration.model.TemplateEvent;
import io.sunshower.service.repository.EntityRepository;
import io.sunshower.service.revision.model.Revision;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;

public interface TemplateService
    extends EntityRepository<Identifier, Template>, GraphService, ObservableSource<TemplateEvent> {

  String NAME = "services:orchestration-template";

  @Override
  void subscribe(Observer<? super TemplateEvent> observer);

  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'READ')")
  List<Property> getProperties(Identifier id);

  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'READ')")
  Graph getGraph(Identifier id, Revision revision);

  @PreAuthorize("hasPermission(#tid, 'io.sunshower.service.orchestration.model.Template', 'READ')")
  Graph getCurrentGraph(Identifier tid);

  @PreAuthorize("hasPermission(#tid, 'io.sunshower.service.orchestration.model.Template', 'WRITE')")
  void saveGraph(Identifier tid, Graph graph);

  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'WRITE')")
  Revision commit(Identifier id);

  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'WRITE')")
  Revision commit(Identifier id, Message message);

  /**
   * @param id
   * @return
   */
  @Override
  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'READ')")
  Template get(Identifier id);

  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'READ')")
  Set<Revision> getRevisions(Identifier id);

  /**
   * @param id
   * @return
   */
  @Override
  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'DELETE')")
  Template delete(Identifier id);

  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'WRITE')")
  Template link(Identifier id, Template template);

  @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.Template', 'WRITE')")
  Template link(Identifier id, Identifier tid);
}
