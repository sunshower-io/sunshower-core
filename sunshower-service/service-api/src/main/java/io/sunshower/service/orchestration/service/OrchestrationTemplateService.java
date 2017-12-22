package io.sunshower.service.orchestration.service;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.sunshower.common.Identifier;
import io.sunshower.service.graph.service.GraphService;
import io.sunshower.service.hal.core.Graph;
import io.sunshower.service.model.Message;
import io.sunshower.service.model.Property;
import io.sunshower.service.orchestration.model.OrchestrationTemplate;
import io.sunshower.service.orchestration.model.TemplateEvent;
import io.sunshower.service.repository.EntityRepository;
import io.sunshower.service.revision.model.Revision;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;
/**
 * Created by haswell on 5/16/17.
 */
public interface OrchestrationTemplateService extends 
        EntityRepository<Identifier, OrchestrationTemplate>, 
        GraphService, 
        ObservableSource<TemplateEvent> {
    
    

    public static final String NAME = "services:orchestration-template";

    @Override
    void subscribe(Observer<? super TemplateEvent> observer);

    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'READ')")
    List<Property<?, ?>> getProperties(Identifier id);

    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'READ')")
    Graph getGraph(Identifier id, Revision revision);


    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'READ')")
    Graph getCurrentGraph(Identifier id);

    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'WRITE')")
    void saveGraph(Identifier id, Graph graph);


    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'WRITE')")
    Revision commit(Identifier id);

    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'WRITE')")
    Revision commit(Identifier id, Message message);

    /**
     * @param id
     * @return
     */
    @Override
    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'READ')")
    OrchestrationTemplate get(Identifier id);


    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'READ')")
    Set<Revision> getRevisions(Identifier id);

    /**
     * @param id
     * @return
     */
    @Override
    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'DELETE')")
    OrchestrationTemplate delete(Identifier id);



    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'WRITE')")
    OrchestrationTemplate link(Identifier id, OrchestrationTemplate template);


    @PreAuthorize("hasPermission(#id, 'io.sunshower.service.orchestration.model.OrchestrationTemplate', 'WRITE')")
    OrchestrationTemplate link(Identifier id, Identifier tid);
}
