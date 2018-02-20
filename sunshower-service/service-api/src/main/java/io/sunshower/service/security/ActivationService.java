package io.sunshower.service.security;

import io.sunshower.model.core.auth.Activation;
import io.sunshower.model.core.auth.User;
import java.util.List;

/** */
public interface ActivationService {

  Activation getActivation();

  boolean isActive();

  List<Activation> list();

  User delete(Activation activation);

  Activation activate(User activator);

  Activation deactivate();
}
