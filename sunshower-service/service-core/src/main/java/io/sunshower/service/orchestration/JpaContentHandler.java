package io.sunshower.service.orchestration;

import io.reactivex.subjects.Subject;
import io.sunshower.common.Identifier;
import io.sunshower.service.git.GitRepository;
import io.sunshower.service.graph.service.ContentResolver;
import io.sunshower.service.hal.core.*;
import io.sunshower.service.hal.core.contents.ContentHandler;
import io.sunshower.service.model.Property;
import io.sunshower.service.model.PropertyAwareObject;
import io.sunshower.service.orchestration.model.Template;
import io.sunshower.service.orchestration.model.TemplateEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.springframework.transaction.PlatformTransactionManager;

public class JpaContentHandler implements ContentHandler {

  private static final String CONTENT_PATH = "orchestrations/%s/%s/%s";
  private static final String GRAPH_PATH = "orchestrations/%s/orchestration.hsf";

  private final Subject<TemplateEvent> topic;

  private Graph graph;
  private Element element;

  final boolean forGraph;

  final Identifier entityId;
  final GitRepository repository;
  final GraphSerializationContext graphContext;
  final Template template;
  final EntityManager entityManager;
  final PlatformTransactionManager transactionManager;

  public JpaContentHandler(
      boolean forGraph,
      Identifier id,
      Subject<TemplateEvent> topic,
      GitRepository repository,
      EntityManager entityManager,
      Template template,
      GraphSerializationContext graphContext,
      PlatformTransactionManager transactionManager) {
    if (!forGraph) {
      Objects.requireNonNull(id);
    }
    this.topic = topic;
    this.entityId = id;
    this.forGraph = forGraph;
    this.template = template;
    this.repository = repository;
    this.graphContext = graphContext;
    this.entityManager = entityManager;
    this.transactionManager = transactionManager;
  }

  public JpaContentHandler(
      Identifier id,
      Subject<TemplateEvent> topic,
      GitRepository repository,
      EntityManager entityManager,
      Template template,
      GraphSerializationContext graphContext,
      PlatformTransactionManager transactionManager) {
    this(false, id, topic, repository, entityManager, template, graphContext, transactionManager);
  }

  @Override
  public ContentHandler setProperties(Collection<Property<?, ?>> properties) {
    check(PropertyInclusion.Graph);
    graph.clearProperties();
    for (Property<?, ?> p : properties) {
      graph.addProperty(p);
    }
    return this;
  }

  @Override
  public ContentHandler setProperties(
      String name, PropertyInclusion property, Collection<Property<?, ?>> properties) {
    check(property);
    switch (property) {
      case Graph:
        setProperties(properties);
        break;
      case Node:
        setPropertiesOn(properties, (PropertyAwareObject<?>) element);
        break;
      case Content:
        setPropertiesOn(properties, contentFor(name));
    }
    return this;
  }

  private void setPropertiesOn(Collection<Property<?, ?>> properties, PropertyAwareObject<?> e) {
    e.clearProperties();
    for (Property<?, ?> p : properties) {
      e.addProperty(p);
    }
  }

  @Override
  public ContentHandler setProperties(String name, Collection<Property<?, ?>> properties) {
    return null;
  }

  @Override
  public Set<Property<?, ?>> getProperties() {
    check(PropertyInclusion.Graph);
    return allProperties();
  }

  @Override
  public Set<Property<?, ?>> getProperties(PropertyInclusion inclusion, String name) {
    check(inclusion);
    switch (inclusion) {
      case Graph:
        return allProperties();
      case Node:
        return nodeProperties();
      case Content:
        return contentProperties(name);
    }
    throw new IllegalStateException("Nope");
  }

  private Set<Property<?, ?>> contentProperties(String name) {
    Set<Content> contents = element.getContents();
    if (contents != null) {
      for (Content c : contents) {
        if (Objects.equals(c.getName(), name)) {
          return new LinkedHashSet<>(c.getProperties());
        }
      }
    }
    return Collections.emptySet();
  }

  private Set<Property<?, ?>> nodeProperties() {
    Set<Content> contents = element.getContents();
    final Map<String, Property<?, ?>> properties = new LinkedHashMap<>();
    if (contents != null) {
      for (Content c : contents) {
        List<Property<?, ?>> cprops = c.getProperties();
        for (Property<?, ?> cprop : cprops) {
          properties.put(cprop.getName(), cprop);
        }
      }
    }
    PropertyAwareObject<?> pao = (PropertyAwareObject<?>) element;
    for (Property<?, ?> p : pao.getProperties()) {
      properties.remove(p.getName());
      properties.put(p.getName(), p);
    }
    return new LinkedHashSet<>(properties.values());
  }

  private Set<Property<?, ?>> allProperties() {

    final Set<Property<?, ?>> results = new LinkedHashSet<>();
    results.addAll(graph.getProperties());
    results.addAll(
        graph
            .getVertices()
            .stream()
            .flatMap(
                t ->
                    Stream.concat(
                        t.getProperties().stream(),
                        t.getContents().stream().flatMap(u -> u.getProperties().stream())))
            .collect(Collectors.toSet()));
    results.addAll(
        graph
            .getEdges()
            .stream()
            .flatMap(
                t ->
                    Stream.concat(
                        t.getProperties().stream(),
                        t.getContents().stream().flatMap(u -> u.getProperties().stream())))
            .collect(Collectors.toSet()));
    return results;
  }

  @Override
  public Set<Content> list() {
    check();
    return element.getContents();
  }

  @Override
  public ContentResolver resolve(Content content) {
    check();
    if (element.getContents().stream().noneMatch(t -> t.equals(content))) {
      throw new IllegalStateException(
          String.format("Content %s does not exist in this graph", content));
    }
    return new FileBasedContentResolver(entityId, Vertex.class, template, content, topic, element);
  }

  @Override
  public ContentHandler addContent(Content content) {
    if (content == null) {
      throw new IllegalArgumentException("Cannot add null content");
    }
    check();
    element.addContent(content);
    create(content);
    return this;
  }

  @Override
  public ContentHandler removeContent(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Cannot remove null content");
    }
    check();
    element.getContents().stream().filter(t -> name.equals(t.getName())).forEach(this::delete);
    return this;
  }

  @Override
  public ContentHandler removeContent(Content content) {
    if (content == null) {
      throw new IllegalArgumentException("Cannot remove null content");
    }
    check();
    if (!element.getContents().removeIf(t -> t.equals(content))) {
      throw new IllegalStateException(
          String.format("Content %s does not exist in this graph", content));
    }
    delete(content);
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ContentResolver resolve(String contentName) {
    check();
    return new FileBasedContentResolver(
        entityId, Vertex.class, template, contentFor(contentName), topic, element);
  }

  private Content contentFor(String contentName) {
    Set<Content> contents = element.getContents();
    return contents
        .stream()
        .filter(t -> Objects.equals(t.getName(), contentName))
        .findFirst()
        .get();
  }

  private void delete(Content content) {
    final Path path = Paths.get(path(content));
    final Path parentDir = path.getParent();
    try {
      if (!path.toFile().delete()) {}
      io.sunshower.io.Files.delete(parentDir.toFile());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void create(Content content) {
    final Path path = Paths.get(path(content));
    final Path parentDir = path.getParent();
    final File parentFile = parentDir.toFile();

    try {
      if (!(parentFile.exists() || parentFile.mkdirs())) {
        throw new RuntimeException("Couldn't create file");
      }
      final File actualFile = path.toFile();
      actualFile.createNewFile();
      content.setFile(new io.sunshower.model.core.io.File(actualFile.getAbsolutePath()));
    } catch (Exception ex) {
      throw new RuntimeException(
          String.format(
              "Failed to create file: \n\t'%s'\nChild of\n\t'%s' (exists=%s).\n\t  Reason: '%s'",
              path.toFile().getAbsolutePath(),
              parentFile.getAbsolutePath(),
              parentFile.exists(),
              ex.getMessage()));
    }
  }

  @Override
  public void close() {
    flush();
  }

  @Override
  public void flush() {
    if (graph == null) {
      throw new IllegalStateException("Content handler is not open!");
    }
    repository.write(path(), graphContext.open(graph));
  }

  @Override
  public Set<Content> destroy() {
    final Set<Content> contents = new HashSet<>(list());
    final Set<Content> destroyed = new HashSet<>();
    for (Content content : contents) {
      try {
        removeContent(content);
      } catch (Exception ex) {
        destroyed.add(content);
      }
    }
    return destroyed;
  }

  private void check() {
    check(PropertyInclusion.Node);
  }

  private void check(PropertyInclusion inclusion) {
    load();
    if (!(forGraph || inclusion == PropertyInclusion.Graph)) {
      resolveElement();
    }
  }

  private String path(Content content) {
    return new File(
            repository.getLocal(),
            String.format(CONTENT_PATH, template.getId(), entityId, content.getName()))
        .getAbsolutePath();
  }

  private String path() {
    return String.format(GRAPH_PATH, template.getId());
  }

  private Graph load() {
    if (graph == null) {
      graph = graphContext.load(repository.read(path()));
    }
    return graph;
  }

  private void resolveElement() {
    for (Vertex v : graph.getVertices()) {
      if (v.getId().equals(entityId)) {
        element = v;
        return;
      }
    }
    for (Edge e : graph.getEdges()) {
      if (e.getId().equals(entityId)) {
        element = e;
        return;
      }
    }
    throw new NoSuchElementException("No content found!");
  }
}
