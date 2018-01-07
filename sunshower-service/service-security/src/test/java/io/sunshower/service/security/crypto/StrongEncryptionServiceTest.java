package io.sunshower.service.security.crypto;

import io.sunshower.common.crypto.Hashes;
import io.sunshower.common.crypto.Multihash;
import io.sunshower.core.security.UserService;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.security.SecurityTest;
import io.sunshower.test.persist.Principal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;

import javax.inject.Inject;
import javax.inject.Named;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;


public class StrongEncryptionServiceTest extends SecurityTest {

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