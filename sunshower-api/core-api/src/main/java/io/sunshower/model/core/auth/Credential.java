package io.sunshower.model.core.auth;

import io.sunshower.persistence.core.DistributableEntity;

import javax.persistence.*;

/**
 * Created by haswell on 5/22/17.
 */
@Entity
@Table(name = "CREDENTIAL")
@Inheritance(
        strategy = InheritanceType.JOINED
)
public abstract class Credential extends ProtectedDistributableEntity {


    /**
     * @return
     */
    public abstract String getSecret();


    /**
     *
     * @return
     */
    public abstract CredentialType getType();

    /**
     *
     * @param secret
     */
    public abstract void setSecret(String secret);
}
