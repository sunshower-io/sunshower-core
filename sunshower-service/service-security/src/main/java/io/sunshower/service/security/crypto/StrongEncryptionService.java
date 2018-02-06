package io.sunshower.service.security.crypto;

import io.sunshower.common.Identifier;
import io.sunshower.common.crypto.Hashes;
import io.sunshower.common.crypto.Multihash;
import io.sunshower.core.security.InvalidCredentialException;
import io.sunshower.core.security.InvalidTokenException;
import io.sunshower.core.security.crypto.EncryptionService;
import io.sunshower.model.core.auth.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.cache.Cache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StrongEncryptionService implements EncryptionService {

  static final Logger log = Logger.getLogger(StrongEncryptionService.class.getName());

  static final Hashes.HashFunction hashFunction = Hashes.create(Multihash.Type.SHA_2_256);

  @Inject
  @Named("caches:authentication")
  private Cache cache;

  @Inject private PasswordEncoder encoder;

  @Inject private TextEncryptor encrypter;

  @PersistenceContext private EntityManager entityManager;

  @Inject private MessageAuthenticationCode messageAuthenticationCode;

  @PostConstruct
  public void postConstruct() {
    log.log(Level.INFO, "Encryption Service using: " + hashFunction);
  }

  @Override
  public String sign(String value) {
    return encrypter.encrypt(value);
  }

  @Override
  public String unsign(String value) {
    return encrypter.decrypt(value);
  }

  @Override
  public String encrypt(String password) {
    if (password == null) {
      throw new IllegalArgumentException("Expected password to encrypt to not be null");
    }
    return encoder.encode(password);
  }

  @Override
  public boolean matches(String raw, String password) {
    return encoder.matches(raw, password);
  }

  @Override
  public String createToken(User user) {
    final String password = encrypter.encrypt(user.getPassword());
    final String id = encrypter.encrypt(user.getId().toString());
    final String combined = id + "#" + password;
    return messageAuthenticationCode.token(combined);
  }

  @Override
  public User findByToken(String token) {
    if (isLogoutRequest(token)) {
      final String[] parts = token.split("\\$\\$");
      final String hashPart = parts[2];
      final Multihash hash = compute(hashPart);
      cache.evict(hash);
      return null;
    } else {
      return findUser(token);
    }
  }

  private boolean isLogoutRequest(String token) {
    return token.startsWith("$$logout$$");
  }

  private User findUser(String token) {
    final Multihash multihash = compute(token);
    Cache.ValueWrapper value = cache.get(multihash);
    if (value != null && value.get() != null) {
      return (User) value.get();
    }

    final String total = messageAuthenticationCode.id(token);
    final String[] parts = total.split("#");

    if (parts.length != 2) {
      throw new InvalidTokenException("Nope");
    }
    final Identifier id = Identifier.valueOf(encrypter.decrypt(parts[0]));
    final String password = encrypter.decrypt(parts[1]);

    User user =
        entityManager
            .createQuery(
                "select u from User u " + "left join fetch u.roles as r " + "where u.id = :id",
                User.class)
            .setParameter("id", id.value())
            .getSingleResult();
    if (password.equals(user.getPassword())) {
      return user;
    }
    throw new InvalidCredentialException("Credential was invalid");
  }

  private Multihash compute(String token) {
    return hashFunction.compute(token.getBytes());
  }
}
