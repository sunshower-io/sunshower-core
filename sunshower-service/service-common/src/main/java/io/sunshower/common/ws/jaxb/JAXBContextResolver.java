package io.sunshower.common.ws.jaxb;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;

public interface JAXBContextResolver {

  JAXBContext getContext(MediaType type, Class<?>... types);

  JAXBContext getContext(Class<?> type, MediaType mediaType);
}
