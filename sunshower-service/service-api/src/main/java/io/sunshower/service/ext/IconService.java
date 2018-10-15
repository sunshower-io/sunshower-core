package io.sunshower.service.ext;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.Image;
import io.sunshower.model.core.ImageAware;

public interface IconService {

  Image iconFor(Object o, int width, int height);

  Image iconDirect(String name, int i, int i1);

  <T extends ImageAware> Image setIcon(Class<T> type, Identifier id, Image image);
}
