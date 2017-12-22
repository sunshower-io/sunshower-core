package io.sunshower.service.git;

import io.sunshower.model.core.auth.Keypair;
import io.sunshower.model.core.auth.Tenant;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.AuthenticatedTestCase;
import io.sunshower.service.Repository;
import io.sunshower.service.model.io.FileResolutionStrategy;
import io.sunshower.service.revision.model.Remote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by haswell on 5/22/17.
 */
public class JGitRepositoryTest extends AuthenticatedTestCase {


    private File resolved;

    private GitRepository repository;


    private FileResolutionStrategy resolver;

    @BeforeEach
    public void setUp() {
        resolver = new TestRepositoryResolver();
        resolved = resolver.resolve(
                new Tenant(),
                new User(),
                new Keypair()
        );
    }

    @Test
    public void ensureResolvingDirectoryRootWorksAndProducesBuild() {
        final TestRepositoryResolver resolver = new TestRepositoryResolver();
        File resolve = resolver.resolve(new Tenant(), new User(), new Keypair());
        assertThat(resolve.getAbsoluteFile().getName(), is("build"));
        assertThat(resolve.isDirectory(), is(true));
    }






}