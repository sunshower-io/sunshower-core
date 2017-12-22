package io.sunshower.service.serialization;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.faults.SystemException;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.service.hal.core.*;
import org.springframework.util.FastByteArrayOutputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.io.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by haswell on 5/24/17.
 */
@SuppressWarnings("unchecked")
public class MOXyJsonGraphContext implements GraphSerializationContext {

    private final DynamicJaxrsProviders providers;
    private final DynamicResolvingMoxyJsonProvider provider;

    public MOXyJsonGraphContext(
            DynamicResolvingMoxyJsonProvider provider, 
            DynamicJaxrsProviders providers
    ) {
        this.provider = provider;
        this.providers = providers;
    }

    @Override
    public GraphSummary loadSummary(InputStream inputStream) {
        try {
            return (GraphSummary) provider.readFrom(
                    (Class) GraphSummary.class,
                    GraphSummary.class,
                    new Annotation[0],
                    MediaType.APPLICATION_JSON_TYPE,
                    new MultivaluedHashMap<>(),
                    inputStream
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream open(Graph graph) {
        final FastByteArrayOutputStream outputStream =
                new FastByteArrayOutputStream(1024);
        try {
            provider.writeTo(
                    graph,
                    Graph.class,
                    Graph.class,
                    new Annotation[0],
                    MediaType.APPLICATION_JSON_TYPE,
                    new MultivaluedHashMap<>(),
                    outputStream
            );
            return outputStream.getInputStream();
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }
    
    public InputStream openSummary(Graph graph) {
        final FastByteArrayOutputStream outputStream =
                new FastByteArrayOutputStream(1024);
        try {
            provider.writeTo(
                    summarize(graph),
                    GraphSummary.class,
                    GraphSummary.class,
                    new Annotation[0],
                    MediaType.APPLICATION_JSON_TYPE,
                    new MultivaluedHashMap<>(),
                    outputStream
            );
            return outputStream.getInputStream();
        } catch (IOException e) {
            throw new SystemException(e);
        }
        
    }

    @Override
    public GraphSummary summarize(Graph graph) {
        final GraphSummary graphSummary = new GraphSummary();
        final Set<? extends DistributableEntity> edges = graph.getEdges();
        final Set<? extends DistributableEntity> vertices = graph.getVertices();
        graphSummary.setEdgeSummary(summarize(edges));
        graphSummary.setVertexSummary(summarize(vertices));
        return graphSummary;
    }

    private ElementSummary summarize(Set<? extends DistributableEntity> elements) {
        ElementSummary result = new ElementSummary();
        if(elements != null) {
            LinkedHashSet<Identifier> ids = new LinkedHashSet<>();
            for(DistributableEntity element : elements) {
                ids.add(element.getId());
            }
            result.setIdentifiers(new ArrayList<>(ids));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Graph load(InputStream inputStream) {
        try {
            return (Graph) provider.readFrom(
                    (Class) Graph.class,
                    Graph.class,
                    new Annotation[0],
                    MediaType.APPLICATION_JSON_TYPE,
                    new MultivaluedHashMap<>(),
                    inputStream
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void registerComponent(Class<T> type) {
        provider.register(Graph.class, type);
    }

}
