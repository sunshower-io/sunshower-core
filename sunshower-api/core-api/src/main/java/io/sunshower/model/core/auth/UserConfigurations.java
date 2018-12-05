package io.sunshower.model.core.auth;

public class UserConfigurations {

  public interface Keys {
    String Timeout = "session::timeout";
  }

  public interface Defaults {
    int Timeout = 60;
  }
}
