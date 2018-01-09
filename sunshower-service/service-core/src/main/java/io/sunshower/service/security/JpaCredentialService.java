package io.sunshower.service.security;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.Credential;
import io.sunshower.service.BaseRepository;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.security.access.prepost.PreAuthorize;

/** Created by haswell on 6/6/17. */
public class JpaCredentialService extends BaseRepository<Identifier, Credential>
    implements CredentialService {

  @Inject private TextEncryptor encryptor;

  public JpaCredentialService() {
    super(Credential.class, "Credential");
  }

  @Override
  @PreAuthorize("hasPermission(#id, 'io.sunshower.model.core.auth.Credential', 'WRITE')")
  public Credential get(Identifier id) {
    return super.get(id);
  }

  @Override
  @PreAuthorize("hasPermission(#id, 'io.sunshower.model.core.auth.Credential', 'DELETE')")
  public Credential delete(Identifier id) {
    return super.delete(id);
  }

  @Override
  @PreAuthorize("hasAuthority('tenant:user')")
  public Credential create(Credential entity) {
    Credential credential = getEntityManager().find(Credential.class, entity.getId());
    if (credential != null) {
      throw new PersistenceException("Cannot modify credential");
    }
    entity.setSecret(encryptor.encrypt(entity.getSecret()));
    return super.create(entity);
  }

  @Override
  public String getSecret(Identifier id) {
    Credential credential = getEntityManager().find(Credential.class, id);
    return encryptor.decrypt(credential.getSecret());
  }
}
