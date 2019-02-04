package io.sunshower.model.core.auth;

import java.util.concurrent.TimeUnit;

public class UserConfigurations {

  public interface Keys {
    String Timeout = "session::timeout";
  }

  public interface Defaults {
    int Timeout = (int) TimeUnit.MINUTES.toMillis(60);
  }
}
