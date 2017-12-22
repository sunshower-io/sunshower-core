package io.sunshower.service.orchestration;

import io.reactivex.subjects.Subject;
import io.sunshower.common.Identifier;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.service.git.GitRepository;
import io.sunshower.service.hal.core.GraphSerializationContext;
import io.sunshower.service.hal.core.contents.ContentHandler;
import io.sunshower.service.hal.core.contents.ContentManager;
import io.sunshower.service.model.Property;
import io.sunshower.service.orchestration.model.OrchestrationTemplate;
import io.sunshower.service.orchestration.model.TemplateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JpaContentManager implements ContentManager {
    
    static final Logger logger = LoggerFactory.getLogger(JpaContentManager.class);

    private final OrchestrationTemplate      template;
    private final GitRepository              repository;
    private final GraphSerializationContext  graphContext;
    private final EntityManager              entityManager;
    private final PlatformTransactionManager transactionManager;
    private final Subject<TemplateEvent>     topic;

    private final Map<Identifier, ContentHandler> openHandlers;

    public JpaContentManager(
            Subject<TemplateEvent> topic,
            GitRepository repository,
            EntityManager entityManager,
            OrchestrationTemplate template,
            GraphSerializationContext graphContext,
            PlatformTransactionManager transactionManager
    ) {
        this.topic = topic;
        this.template = template;
        this.repository = repository;
        this.openHandlers = new HashMap<>();
        this.graphContext = graphContext;
        this.entityManager = entityManager;
        this.transactionManager = transactionManager;
    }

    @Override
    public void close(DistributableEntity entity) {
        Objects.requireNonNull(entity, "Entity must not be null");
        close(entity.getId());
    }

    @Override
    public void close(Identifier id) {
        final ContentHandler handler = openHandlers.get(id);
        if(handler != null) {
            handler.close();
        }
    }

    @Override
    public void close() {
        for(Identifier o : openHandlers.keySet()) {
            close(o);
        }
        try {
            repository.close();
        } catch (Exception e) {
            logger.warn("Failed to close repository.  Reason:", e);
        }
    }

    @Override
    public ContentHandler graphContent() {
        final ContentHandler handler = new JpaContentHandler(
                true, 
                null,
                topic,
                repository,
                entityManager,
                template,
                graphContext,
                transactionManager
        );
        openHandlers.put(Identifier.random(), handler);
        return handler;
    }

    @Override
    public ContentHandler contentFor(DistributableEntity e) {
        Objects.requireNonNull(e);
        final ContentHandler handler = new JpaContentHandler(
                e.getId(),
                topic,
                repository,
                entityManager,
                template,
                graphContext,
                transactionManager
        );
        openHandlers.put(e.getId(), handler);
        return handler;
    }

    @Override
    public ContentHandler contentFor(Identifier id, Class<?> type) {
        Objects.requireNonNull(id);
        final ContentHandler handler = new JpaContentHandler(
                id,
                topic,
                repository,
                entityManager,
                template,
                graphContext,
                transactionManager
        );
        openHandlers.put(id, handler);
        return handler;

    }
}
