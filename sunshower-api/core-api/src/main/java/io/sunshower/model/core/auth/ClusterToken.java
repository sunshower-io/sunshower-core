package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Cacheable
@Table(name = "CLUSTER_TOKEN", schema = "SUNSHOWER")
public class ClusterToken extends DistributableEntity {
  @Basic
  @Column(name = "token")
  private String token;
}
