package io.sunshower.service.security.crypto;

import io.sunshower.common.crypto.Hashes;
import io.sunshower.common.crypto.Multihash;
import io.sunshower.core.security.UserService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.jpa.flyway.FlywayConfiguration;
import io.sunshower.model.core.auth.User;
import io.sunshower.persist.core.DataSourceConfiguration;
import io.sunshower.persist.hibernate.HibernateConfiguration;
import io.sunshower.service.security.SecurityConfiguration;
import io.sunshower.test.security.AuthenticationTestExecutionListener;
import io.sunshower.test.security.Principal;
import io.io.sunshower.service.security.TestSecurityConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

/**
 * Created by haswell on 3/5/17.
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {
                SecurityConfiguration.class,
                HibernateConfiguration.class,
                FlywayConfiguration.class,
                DataSourceConfiguration.class,
                TestSecurityConfiguration.class
        })
@TestExecutionListeners(listeners = {
        ServletTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class,
        AuthenticationTestExecutionListener.class
})
@Rollback
@Transactional
public class StrongEncryptionServiceTest {

    @Inject
    @Named("caches:authentication")
    private Cache authenticationCache;

    @Inject
    private UserService userService;

    @Inject
    private EncryptionService encryptionService;

    @Principal
    public User user() {
        final User u = new User();
        u.setUsername("josiah");
        u.setPassword("adapasdfasdf");
        u.getDetails().setEmailAddress("adfasdf");
        return u;
    }

    @Test
    public void ensureCacheIsHit() {
        User josiah = userService.findByUsername("josiah");


        String next = encryptionService.createToken(josiah);
        encryptionService.findByToken(next);

        Mockito.verify(
                authenticationCache, times(1)
        ).get(Hashes.create(Multihash.Type.SHA_2_256)
                .compute(next.getBytes()));


    }


    @Test
    public void ensureCacheIsInvalidated() {

        User josiah = userService.findByUsername("josiah");
        String next = encryptionService.createToken(josiah);
        encryptionService.findByToken(next);


        Mockito.verify(
                authenticationCache, times(1)
        ).get(Hashes.create(Multihash.Type.SHA_2_256)
                .compute(next.getBytes()));

        String logout = "$$logout$$" + next;
        encryptionService.findByToken(logout);


        Mockito.verify(
                authenticationCache, times(1)
        ).evict(Hashes.create(Multihash.Type.SHA_2_256)
                .compute(next.getBytes()));

    }
    
    

    @Test
    public void ensureMultihashIsCachedCorrectly() {
        final String logout = "$$logout$$WlI3cW5Vb1lKMkhyUTM1SmRYTUVKZ3BuVH" +
                "Rjc1dlK3p2K1h1dE9jdVFQSE1yYnhXYmUyRml0VlRRWHJ4MUprTCN6UkV5eWxWV2ZmSW56dj" +
                "I4a1RQcXFTUW9oTE1FUFE4NTBrNHJYekgvSWtUalpxSk41d0tqSmlqNDBia244UWxrei91NGxJS1podmdqMHd" +
                "qQXlJYmZGQ1BlL3RweGIyZE0=:" +
                "IXE+5qY5ORkf6fmiEBZIqCm2SIfc57HbrXq7VB3Fq0A=";
        final String[] parts = logout.split("\\$\\$");
        assertThat(parts[1], is("logout"));
    }

}