package io.sunshower.service.git;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.Credential;
import io.sunshower.model.core.auth.Tenant;
import io.sunshower.model.core.auth.User;
import io.sunshower.persist.Identifiers;
import io.sunshower.service.model.io.FileResolutionStrategy;

import java.io.File;
import java.util.UUID;

/**
 * Created by haswell on 5/24/17.
 */
public class MockRepositoryResolutionStrategy implements FileResolutionStrategy {

    @Override
    public File resolve(Tenant tenant, User user, Credential credential) {
        File resolve = new TestRepositoryResolver().resolve(tenant, user, credential);
        return new File(resolve, Identifier.random().toString());
    }
}
