package io.sunshower.service.git;

import io.sunshower.service.revision.model.Repository;

/**
 * Created by haswell on 5/22/17.
 */
public interface RepositoryService {

    GitRepository open(Repository repository);
}
