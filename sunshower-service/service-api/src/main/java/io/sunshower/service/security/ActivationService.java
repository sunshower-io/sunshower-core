package io.sunshower.service.security;

/** */
public interface ActivationService {

  Activation getActivation();

  boolean isActive();

  Activation activate(Activation activation);

  Activation deactivate(Activation activation);
}
