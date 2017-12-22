package io.sunshower.service.io;

import io.sunshower.model.core.auth.Keypair;
import io.sunshower.model.core.auth.Tenant;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.git.MockRepositoryResolutionStrategy;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by haswell on 5/25/17.
 */
public class DefaultConfigurableFileResolutionStrategyDefaultTest {

    static {
        File resolve = new MockRepositoryResolutionStrategy().resolve(
                new Tenant(),
                new User(),
                new Keypair()
        );
        System.setProperty("user.dir", resolve.getAbsolutePath());
    }

    @Test
    public void ensureResolutionIsCorrect() {
        File resolve = new DefaultConfigurableFileResolutionStrategy().resolve(
                new Tenant(),
                new User(),
                new Keypair()
        );
        assertThat(resolve.exists(), is(true));
    }

}