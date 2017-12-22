package io.sunshower.model.core.auth;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by haswell on 5/22/17.
 */
@Entity
@Table(name = "USERNAME_PASSWORD_CREDENTIAL")
public class UsernamePasswordCredential extends Credential {


    @Basic
    @NotNull
    private String username;

    @Basic
    @NotNull
    private String password;

    public UsernamePasswordCredential() {
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String getSecret() {
        return password;
    }

    @Override
    public CredentialType getType() {
        return CredentialType.UsernamePassword;
    }

    @Override
    public void setSecret(String secret) {
        this.setPassword(secret);
    }
}
