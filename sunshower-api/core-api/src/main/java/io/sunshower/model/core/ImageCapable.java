package io.sunshower.model.core;

import javax.annotation.Nonnull;

/** my kingdom for multiple inheritance here */
public interface ImageCapable {

  Image getImage();

  void setImage(@Nonnull Image image);
}
