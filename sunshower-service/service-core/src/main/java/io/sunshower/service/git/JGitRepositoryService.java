package io.sunshower.service.git;

import io.sunshower.model.core.auth.User;
import io.sunshower.service.revision.model.Repository;
import io.sunshower.service.security.Session;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by haswell on 5/24/17.
 */
public class JGitRepositoryService implements RepositoryService {
    @Inject
    private Session session;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public GitRepository open(Repository repository) {
        return new JGitRepository(
                repository,
                entityManager.find(User.class, session.getId())
        );
    }
}
