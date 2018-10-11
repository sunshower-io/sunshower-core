package io.sunshower.model.core.auth;

import javax.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class ImageAware extends ProtectedDistributableEntity {

  @JoinColumn(name = "image_id")
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Image image;

  public void setImage(@NonNull Image i) {
    this.image = i;
  }
}
