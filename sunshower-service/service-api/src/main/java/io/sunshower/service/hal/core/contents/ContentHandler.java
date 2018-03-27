package io.sunshower.service.hal.core.contents;

import io.sunshower.service.graph.service.ContentResolver;
import io.sunshower.service.hal.core.Content;
import io.sunshower.service.model.Property;
import java.util.Collection;
import java.util.Set;

public interface ContentHandler extends AutoCloseable {

  enum PropertyInclusion {
    Graph,
    Node,
    Content
  }

  ContentHandler setProperties(Collection<Property> properties);

  ContentHandler setProperties(
      String name, PropertyInclusion property, Collection<Property> properties);

  ContentHandler setProperties(String name, Collection<Property> properties);

  Set<Property> getProperties();

  Set<Property> getProperties(PropertyInclusion inclusion, String name);

  Set<Content> list();

  ContentResolver resolve(Content content);

  ContentHandler addContent(Content content);

  ContentHandler removeContent(Content clusterDefinition);

  ContentHandler removeContent(String contentName);

  ContentResolver resolve(String contentName);

  void close();

  void flush();

  Set<Content> destroy();
}
