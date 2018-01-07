package io.sunshower.service.revision.model;

import io.sunshower.common.io.Files;
import io.sunshower.model.core.auth.ProtectedDistributableEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by haswell on 5/22/17.
 */
@Entity
@Table(name = "GIT_REPOSITORY")
public class Repository extends ProtectedDistributableEntity {

    @NotNull
    @OneToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "local_id")
    private Local local;

    @OneToOne(
            cascade = CascadeType.ALL
    )
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
