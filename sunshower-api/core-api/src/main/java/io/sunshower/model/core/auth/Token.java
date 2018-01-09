package io.sunshower.model.core.auth;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/** Created by haswell on 10/18/16. */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Token {

  @XmlAttribute private String token;

  @XmlAttribute private Date expiration;

  public Token() {}

  public Token(String token, Date expiration) {
    this.token = token;
    this.expiration = expiration;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Date getExpiration() {
    return expiration;
  }

  public void setExpiration(Date expiration) {
    this.expiration = expiration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Token)) return false;

    Token token1 = (Token) o;

    if (token != null ? !token.equals(token1.token) : token1.token != null) return false;
    return expiration != null ? expiration.equals(token1.expiration) : token1.expiration == null;
  }

  @Override
  public int hashCode() {
    int result = token != null ? token.hashCode() : 0;
    result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Token{" + "token='" + token + '\'' + ", expiration=" + expiration + '}';
  }
}
