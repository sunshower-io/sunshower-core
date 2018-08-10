package io.sunshower.service.signup;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.User;
import java.util.List;

public interface SignupService {
  /** @return */
  List<User> list();

  /**
   * @param userId
   * @return
   */
  User revoke(Identifier userId);

  /**
   * @param requestId
   * @return
   */
  User approve(String requestId);

  /**
   * @param input
   * @return
   */
  RegistrationRequest signup(User input);

  RegistrationRequest signup(User input, List<String> productIds);

  /** @return */
  List<RegistrationRequest> pendingRegistrations();
}
