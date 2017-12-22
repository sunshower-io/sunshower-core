package io.sunshower.service.security;

import io.sunshower.model.core.Application;
import io.sunshower.model.core.auth.User;

import javax.ws.rs.*;
import java.util.Set;

/**
 * Created by haswell on 3/14/17.
 */
public interface ApplicationService {
    /**
     *
     * @return
     */
    Application instance();

    /**
     *
     * @return
     */
    Boolean isInitialized();

    /**
     *
     * @param var1
     * @return
     */
    Application initialize(Application var1);

    /**
     *
     * @return
     */
    Set<User> getAdministrators();

    /**
     *
     * @param var1
     * @return
     */
    Boolean addAdministrator(User var1);

    /**
     *
     * @param var1
     * @return
     */
    Boolean removeAdministrator(User var1);
}
