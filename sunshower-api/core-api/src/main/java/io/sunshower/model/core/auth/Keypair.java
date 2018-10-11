package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "KEYPAIR_CREDENTIAL", schema = Schemata.SUNSHOWER)
public class Keypair extends Credential {

  @Basic private String key;

  @Basic private String secret;

  public Keypair() {
    setVisibility(Visibility.Private);
  }

  @Override
  public CredentialType getType() {
    return CredentialType.KeyPair;
  }
}
