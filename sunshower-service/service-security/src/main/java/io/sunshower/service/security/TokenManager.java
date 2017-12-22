package io.sunshower.service.security;


/**
 * Created by haswell on 3/5/17.
 */
public interface TokenManager {

    void checkEncoded(String token);

    void check(String token);
}
