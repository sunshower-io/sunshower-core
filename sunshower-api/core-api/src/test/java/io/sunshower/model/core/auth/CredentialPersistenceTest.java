package io.sunshower.model.core.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sunshower.model.core.PersistenceTest;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

public class CredentialPersistenceTest extends PersistenceTest {

  @PersistenceContext private EntityManager entityManager;

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
