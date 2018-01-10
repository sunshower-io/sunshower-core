package io.sunshower.service.security;

import io.sunshower.model.core.auth.Activation;
import io.sunshower.model.core.auth.User;

/** */
public interface ActivationService {

  Activation getActivation();

  boolean isActive();

  Activation activate(User activator);

  Activation deactivate();
}
