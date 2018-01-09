package io.sunshower.service.model.io;

import io.sunshower.model.core.auth.Credential;
import io.sunshower.model.core.auth.Tenant;
import io.sunshower.model.core.auth.User;
import java.io.File;

/** Created by haswell on 5/22/17. */
public interface FileResolutionStrategy {

  File resolve(Tenant tenant, User user, Credential credential);
}
