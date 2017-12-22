package io.sunshower.service.security;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.Credential;
import io.sunshower.model.core.auth.Keypair;
import io.sunshower.service.BaseRepository;
import io.sunshower.service.BaseRepositoryTest;
import org.jasypt.util.text.TextEncryptor;

import javax.inject.Inject;


/**
 * Created by haswell on 6/6/17.
 */
public class JpaCredentialServiceTest extends BaseRepositoryTest<Identifier, Credential> {
    @Inject
    private TextEncryptor encryptionService;

    @Inject
    private CredentialService credentialService;


    @Override
    protected Identifier randomId() {
        return Identifier.random();
    }

    @Override
    protected Credential randomEntity() {
        final Keypair keypair = new Keypair();
        keypair.setKey(Identifier.random().toString());
        keypair.setSecret(Identifier.random().toString());
        return keypair;
    }

    @Override
    protected void alter(Credential random) {
        ((Keypair) random).setSecret("hello");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BaseRepository<Identifier, Credential> service() {
        return (BaseRepository<Identifier, Credential>) credentialService;
    }

    @Override
    protected void expectAlteration(Identifier uuid, Credential random) {
//        String secret = ((Keypair) random).getSecret();
//        assertThat(encryptionService.decrypt(secret), is("hello"));
    }

    @Override
    protected void expectSameProperties(Credential random, Credential save) {

    }
}