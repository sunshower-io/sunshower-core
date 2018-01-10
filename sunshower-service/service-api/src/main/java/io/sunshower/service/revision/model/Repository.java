package io.sunshower.service.revision.model;

import io.sunshower.model.core.Schemata;
import io.sunshower.model.core.auth.ProtectedDistributableEntity;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "GIT_REPOSITORY", schema = Schemata.SUNSHOWER)
public class Repository extends ProtectedDistributableEntity {

  @NotNull
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "local_id")
  private Local local;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "remote_id")
  private Remote remote;

  public Path resolve(String p) {
    return Paths.get(local.getFile().getPath(), p);
  }

  public Local getLocal() {
    return local;
  }

  public void setLocal(Local local) {
    this.local = local;
  }

  public Remote getRemote() {
    return remote;
  }

  public void setRemote(Remote remote) {
    this.remote = remote;
  }
}
