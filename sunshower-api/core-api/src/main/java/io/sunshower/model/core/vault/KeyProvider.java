package io.sunshower.model.core.vault;

/**
 * Do not expose via REST or any other mechanism. A key is usually generated every time a node comes
 * online. Node identity might be an ok choice for a key, or an HMAC generated from a principal, or
 * a secure, random number.
 */
public interface KeyProvider {

  String secureString(int length);

  String getKey();

  void regenerate();
}
