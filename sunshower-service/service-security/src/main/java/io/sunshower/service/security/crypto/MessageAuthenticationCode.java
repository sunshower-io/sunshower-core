package io.sunshower.service.security.crypto;

import io.sunshower.core.security.InvalidTokenException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Created by haswell on 10/18/16. */
public class MessageAuthenticationCode {

  public enum Algorithm {
    MD5("HmacMD5"),
    SHA1("HmacSHA1"),
    SHA256("HmacSHA256");

    final String name;

    private Algorithm(final String name) {
      this.name = name;
    }

    public String value() {
      return this.name;
    }
  }

  private final Mac messageCode;

  public MessageAuthenticationCode(Algorithm algorithm, String secret) {
    try {
      this.messageCode = Mac.getInstance(algorithm.name);
      this.messageCode.init(new SecretKeySpec(secret.getBytes(), algorithm.name));
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalStateException(e);
    }
  }

  private synchronized byte[] createKey(byte[] content) {
    return this.messageCode.doFinal(content);
  }

  public String id(String token) {
    if (token != null) {
      final Base64.Decoder decoder = Base64.getDecoder();
      final String[] parts = token.split(":");
      if (parts.length == 2) {
        final byte[] id = decoder.decode(parts[0]);
        final byte[] encrypted = decoder.decode(parts[1]);
        final byte[] hashedBytes = createKey(id);
        if (Arrays.equals(encrypted, hashedBytes)) {
          return new String(id);
        } else {
          throw new InvalidTokenException("Error: provided signature did not match.");
        }
      } else {
        throw new InvalidTokenException("Invalid token format: '" + token + "'");
      }
    }
    throw new InvalidTokenException("Token must not be null");
  }

  public String token(String input) {
    final Base64.Encoder encoder = Base64.getEncoder();
    final byte[] agenthash = encoder.encode(createKey(input.getBytes()));
    final byte[] encodedData = encoder.encode(input.getBytes());
    return new String(encodedData) + ":" + new String(agenthash);
  }
}
