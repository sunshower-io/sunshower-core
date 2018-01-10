package io.sunshower.service.revision.model;

import io.sunshower.model.core.Schemata;
import io.sunshower.model.core.io.File;
import io.sunshower.persistence.core.DistributableEntity;
import io.sunshower.persistence.core.converters.ClassConverter;
import io.sunshower.service.model.io.FileResolutionStrategy;
import javax.persistence.*;

@Entity
@Table(name = "GIT_LOCAL", schema = Schemata.SUNSHOWER)
public class Local extends DistributableEntity {

  @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
  @JoinColumn(name = "file_id")
  private File file;

  @Basic
  @Column(name = "resolution_strategy")
  @Convert(converter = ClassConverter.class)
  private Class<? extends FileResolutionStrategy> resolutionStrategy;

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public Class<? extends FileResolutionStrategy> getResolutionStrategy() {
    return resolutionStrategy;
  }

  public void setResolutionStrategy(Class<? extends FileResolutionStrategy> resolutionStrategy) {
    this.resolutionStrategy = resolutionStrategy;
  }
}
