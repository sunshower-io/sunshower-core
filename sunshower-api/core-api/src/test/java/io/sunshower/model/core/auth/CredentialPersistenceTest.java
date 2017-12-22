package io.sunshower.model.core.auth;

import io.sunshower.model.core.PersistenceTest;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by haswell on 5/22/17.
 */
public class CredentialPersistenceTest extends PersistenceTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void ensureSavingKeypairWorks() {
        final Keypair keypair = new Keypair();
        keypair.setKey("setPassword");
        keypair.setSecret("coolbeans");
        entityManager.persist(keypair);
        entityManager.flush();
    }

    @Test
    public void ensureActualKeypairIsOfCorrectType() {
        final Keypair keypair = new Keypair();
        keypair.setKey("mykeypair");
        keypair.setSecret("whatever");
        entityManager.persist(keypair);

        Credential credential = entityManager.find(Credential.class, keypair.getId());
        assertThat(credential instanceof Keypair, is(true));

    }

    @Test
    public void ensureSavingCredentialWorks() {
        final UsernamePasswordCredential credential = new UsernamePasswordCredential();
        credential.setUsername("joe");
        credential.setPassword("frap");

        entityManager.persist(credential);
        entityManager.flush();
    }

}