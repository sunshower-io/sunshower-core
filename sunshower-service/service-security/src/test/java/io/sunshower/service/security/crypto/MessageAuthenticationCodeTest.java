package io.sunshower.service.security.crypto;

import io.sunshower.core.security.InvalidTokenException;
import io.sunshower.model.core.vault.KeyProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


/**
 * Created by haswell on 10/20/16.
 */
@RunWith(
    SpringJUnit4ClassRunner.class
)
@Configuration
@ContextConfiguration(
    classes = MessageAuthenticationCodeTest.class
)
public class MessageAuthenticationCodeTest {

    @Inject
    private KeyProvider keyProvider;


    @Inject
    private MessageAuthenticationCode mac;

    @Test
    public void ensureGeneratorIsInjected() {
        assertThat(keyProvider, is(not(nullValue())));
    }

    @Test
    public void ensureThatMacIsInjected() {
        assertThat(mac, is(not(nullValue())));
    }



    @Test
    public void ensureThatMacProducesCorrectId() {
        String id = UUID.randomUUID().toString();
        String token = mac.token(id);
        System.out.println(token);
        String computedId = mac.id(token);
        assertThat(id, is(computedId));
    }

    @Test(expected = InvalidTokenException.class)
    public void ensureInvalidMacThrowsException() {
        mac.id("frap:dap");
    }



    @Bean
    public InstanceSecureKeyGenerator generator() {
        return new InstanceSecureKeyGenerator();
    }


    @Bean
    public MessageAuthenticationCode messageAuthenticationCode(InstanceSecureKeyGenerator tokenProvider) {
        return new MessageAuthenticationCode(
                MessageAuthenticationCode.Algorithm.SHA256,
                tokenProvider.getKey()
        );
    }

}