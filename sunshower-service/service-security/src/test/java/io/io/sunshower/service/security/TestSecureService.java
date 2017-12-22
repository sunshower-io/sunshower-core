package io.io.sunshower.service.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;

/**
 * Created by haswell on 10/15/16.
 */

@Service
public class TestSecureService {

    @PreAuthorize("hasAuthority('tenant:user')")
    public void sayHelloTenantUser() {

    }

    @PreAuthorize("hasAuthority('admin') || hasRole('admin')")
    public String sayHelloAdmin() {
        return "hello";
    }

    @PreAuthorize("hasAuthority('user')")
    public String sayHelloUser() {
        return "World";
    }

    @RolesAllowed("ROLE_ADMIN")
    public String sayHelloRolesAllowed() {
        return "Frap";
    }
}
