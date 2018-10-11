package io.sunshower.model.core.auth;

import io.sunshower.model.core.Schemata;
import javax.persistence.*;

@Entity
@Table(name = "CREDENTIAL", schema = Schemata.SUNSHOWER)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Credential extends ProtectedDistributableEntity {

  public abstract String getSecret();

  public abstract CredentialType getType();

  public abstract void setSecret(String secret);
}
