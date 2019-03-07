package io.sunshower.vault.api.secrets;

import io.sunshower.barometer.jaxrs.SerializationAware;
import io.sunshower.barometer.jaxrs.SerializationTestCase;
import io.sunshower.vault.api.Secret;
import io.sunshower.vault.api.SecretMetadata;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by haswell on 11/2/16.
 */
public class CredentialSecretTest extends SerializationTestCase {

    public CredentialSecretTest() {
        super(SerializationAware.Format.JSON, true, CredentialSecret.class, Secret.class, SecretMetadata.class);
    }

    @Test
    public void ensureSerializationWorks() {
        Secret secret = new CredentialSecret();
        secret.set("frap", "adap");
        System.out.println(write(secret));
    }

    @Test
    public void ensureSerializationWithMetadataWorks() {

        CredentialSecret secret = new CredentialSecret();
        secret.setDescription("frap");
        secret.setName("coolname");
        secret.setCredential("credential");
        secret.setSecret("secret");
        secret.set("cool", "beans");
        secret.set("type", "iam");
        String s = write(secret);
        System.out.println(s);

        Secret copy = read(s);

    }

    @Test
    public void ensureSerializationPreservesId() {
//a1c29a72-bfcd-448c-a3b3-00b8ea30b749

        CredentialSecret secret = new CredentialSecret();
        secret.setDescription("frap");
        secret.setName("coolname");
        secret.setCredential("coolbeans");
        secret.set("cool", "beans");
        secret.set("type", "iam");

        UUID id = secret.getId();
        assertThat(id, is(not(nullValue())));
        String s = write(secret);

        Secret copy = read(s);
        assertThat(copy.getId(), is(id));
    }


}