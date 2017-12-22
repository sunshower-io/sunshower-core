package io.sunshower.service.model;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * Created by haswell on 5/9/17.
 */
public class UpdateListener {

    @PreUpdate
    @PrePersist
    public void setLastModified(Updatable updatable) {
        updatable.setLastModified(new Date());
    }
}
