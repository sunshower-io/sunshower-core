package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;
import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "IMAGE", schema = Schemata.SUNSHOWER)
public class Image extends DistributableEntity {

  @Basic(fetch = FetchType.EAGER)
  @Column(name = "image_data")
  private byte[] data;

  @Enumerated private ImageType type;
}
