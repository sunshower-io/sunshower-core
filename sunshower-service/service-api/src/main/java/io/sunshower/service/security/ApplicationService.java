package io.sunshower.service.security;

import io.sunshower.model.core.Application;
import io.sunshower.model.core.auth.User;
import java.util.Set;
import javax.ws.rs.*;

public interface ApplicationService extends ActivationService {
  Application instance();

  Boolean isInitialized();

  Application initialize(Application var1);

  Set<User> getAdministrators();

  Boolean addAdministrator(User var1);

  Boolean removeAdministrator(User var1);
}
