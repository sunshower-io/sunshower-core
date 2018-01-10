package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "KEYPAIR_CREDENTIAL", schema = Schemata.SUNSHOWER)
public class Keypair extends Credential {

  @Basic private String key;

  @Basic private String secret;

  public Keypair() {}

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  @Override
  public CredentialType getType() {
    return CredentialType.KeyPair;
  }
}
