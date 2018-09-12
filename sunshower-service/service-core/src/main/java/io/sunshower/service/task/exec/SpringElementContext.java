package io.sunshower.service.task.exec;

import io.sunshower.service.task.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.w3c.dom.Element;

public class SpringElementContext implements ElementContext {

  static final Logger log = Logger.getLogger(SpringElementContext.class.getName());

  private final Map<String, Class<?>> types;

  public SpringElementContext() {
    types = new HashMap<>();
  }

  @Override
  public <T> void transform(T graph, Class<T> type) {}

  @Override
  public void register(String key, Class<?> type) {
    this.types.put(key, type);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> ElementDescriptor<T> resolve(Node v, TaskGraph taskGraph, ContextResolver resolver) {
    final Class<?> elementType = resolveType(v);
    final Object unprocessedElement = create(elementType, v, taskGraph);
    return (ElementDescriptor<T>)
        new ElementDescriptor(unprocessedElement, elementType, v.getName(), v.getId());
  }

  @Override
  public Relationship resolveRelationship(Edge edge, TaskGraph result) {
    return new Relationship("run-before");
  }

  private Object create(Class<?> elementType, Node v, TaskGraph taskGraph) {
    if (!v.isValueRaw()) {
      return v.getValue();
    }
    try {

      JAXBContext jaxbContext =
          JAXBContextFactory.createContext(new Class[] {elementType}, new Properties());
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      unmarshaller.setProperty("eclipselink.json.include-root", false);
      String format = taskGraph.getFormat();
      if (format == null) {
        format = "application/json";
      }
      unmarshaller.setProperty("eclipselink.media-type", format);
      JAXBElement<?> unmarshal = unmarshaller.unmarshal((Element) v.getValue(), elementType);
      return unmarshal.getName();
    } catch (Exception ex) {
      log.warning(ex.getMessage());
      if (ex instanceof RuntimeException) {
        throw (RuntimeException) ex;
      }
      throw new RuntimeException(ex);
    }
  }

  private Class<?> resolveType(Node v) {
    final String typeKey = v.getKey();
    final Class<?> elementType = types.get(typeKey);
    if (elementType == null) {
      return NullType.class;
    }
    return elementType;
  }
}
