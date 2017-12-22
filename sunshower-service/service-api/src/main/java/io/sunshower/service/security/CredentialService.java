package io.sunshower.service.security;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.Credential;
import io.sunshower.service.repository.EntityRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

/**
 * Created by haswell on 6/6/17.
 */
public interface CredentialService extends EntityRepository<Identifier, Credential> {

    @PreAuthorize("hasPermission('#id', 'io.sunshower.model.core.auth.Credential', 'READ')")
    String getSecret(Identifier id);

    @Override
    @PreAuthorize("hasPermission(#id, 'io.sunshower.model.core.auth.Credential', 'WRITE')")
    Credential get(Identifier id);

    @Override
    @PreAuthorize("hasPermission(#id, 'io.sunshower.model.core.auth.Credential', 'WRITE')")
    Credential delete(Identifier id);
}
