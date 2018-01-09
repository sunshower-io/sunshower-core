package io.sunshower.service.hal.core.contents;

import io.sunshower.common.Identifier;
import io.sunshower.persistence.core.DistributableEntity;

public interface ContentManager extends AutoCloseable {

  void close(DistributableEntity entity);

  void close(Identifier id);

  void close();

  ContentHandler graphContent();

  ContentHandler contentFor(DistributableEntity e);

  ContentHandler contentFor(Identifier id, Class<?> type);
}
