package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;

/**
 * Created by haswell on 5/9/17.
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ProtectedDistributableEntity extends DistributableEntity {


    @OneToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name =  "id",
            insertable = false,
            updatable = false
    )
    private ObjectIdentity identity;


    public ObjectIdentity getIdentity() {
        return identity;
    }

    public void setIdentity(ObjectIdentity identity) {
        this.identity = identity;
    }
}
