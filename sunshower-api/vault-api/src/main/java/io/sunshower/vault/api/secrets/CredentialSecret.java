package io.sunshower.vault.api.secrets;

import io.sunshower.model.core.auth.User;
import io.sunshower.vault.api.Secret;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
;

/**
 * Created by haswell on 10/28/16.
 */
@Entity
@XmlRootElement
@Table(name = "CREDENTIAL_SECRET")
@XmlDiscriminatorValue(
        "io.sunshower.vault.api.secrets.CredentialSecret"
)
public class CredentialSecret extends Secret {

    @Basic
    @XmlAttribute
    private String name;

    @Basic
    @XmlAttribute
    private String credential;

    @Basic
    @XmlAttribute
    private String secret;

    @Basic
    @XmlElement
    private String description;

    @Basic
    @XmlElement
    private Date created;

    @Basic
    @XmlElement
    private Date updated;

    @OneToOne
    @XmlElement
    private User modifier;


    public CredentialSecret() {
        super(UUID.randomUUID());
    }


    public CredentialSecret(UUID id) {
        super(id);
    }

    public CredentialSecret(CredentialSecret s) {
        setCredential(s.credential);
        setSecret(s.secret);
        setName(s.name);
        setDescription(s.description);
    }

    @Override
    protected void setDefaults() {

    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }


    @Override
    public String toString() {
        return "CredentialSecret{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", credential='" + credential + '\'' +
                ", secret='" + secret + '\'' +
                ", description='" + description + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", modifier=" + modifier +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public User getModifier() {
        return modifier;
    }

    public void setModifier(User modifier) {
        this.modifier = modifier;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
