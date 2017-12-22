package io.sunshower.vault.api;

import io.sunshower.model.core.Metadata;
import io.sunshower.vault.api.secrets.CredentialSecret;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
;

/**
 * Created by haswell on 11/1/16.
 */
@Entity
@Table(name = "CREDENTIAL_METADATA")
@XmlDiscriminatorValue(
        "io.sunshower.vault.api.secrets.CredentialSecret"
)
public class SecretMetadata extends Metadata {

    @OneToOne(
            mappedBy = "metadata",
            targetEntity = CredentialSecret.class
    )
    private Secret secret;

    public SecretMetadata() {
        super(UUID.randomUUID());
    }


    @Override
    protected void setDefaults() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecretMetadata)) return false;

        SecretMetadata that = (SecretMetadata) o;
        final UUID id = getId();

        return id != null ? id.equals(that.getId()) : that.getId() == null;

    }

    @Override
    public String toString() {
        return "SecretMetadata{" +
                "id=" + getId()+
                ", secret=" + secret +
                '}';
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
