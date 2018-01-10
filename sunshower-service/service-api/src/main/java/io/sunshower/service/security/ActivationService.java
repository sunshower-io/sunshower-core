package io.sunshower.service.security;

import io.sunshower.model.core.auth.Activation;

/** */
public interface ActivationService {

  Activation getActivation();

  boolean isActive();

  Activation activate(Activation activation);

  Activation deactivate(Activation activation);
}
