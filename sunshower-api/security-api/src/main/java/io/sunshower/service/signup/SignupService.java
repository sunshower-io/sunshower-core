package io.sunshower.service.signup;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.User;

import java.util.List;

/**
 * Created by haswell on 10/17/16.
 */
public interface SignupService {


    /**
     *
     * @return
     */
    List<User> list();

    /**
     *
     * @param userId
     * @return
     */
    User revoke(Identifier userId);

    /**
     *
     * @param requestId
     * @return
     */
    User approve(String requestId);


    /**
     *
     * @param input
     * @return
     */
    RegistrationRequest signup(User input);

    /**
     *
     * @return
     */
    List<RegistrationRequest> pendingRegistrations();




}
