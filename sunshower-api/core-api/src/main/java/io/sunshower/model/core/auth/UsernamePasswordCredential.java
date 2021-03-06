package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "USERNAME_PASSWORD_CREDENTIAL", schema = Schemata.SUNSHOWER)
public class UsernamePasswordCredential extends Credential {

  @Basic @NotNull private String username;

  @Basic @NotNull private String password;

  public UsernamePasswordCredential() {
    setVisibility(Visibility.Private);
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
    this.password = secret;
  }
}
