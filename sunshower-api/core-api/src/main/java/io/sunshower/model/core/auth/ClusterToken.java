package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
