package io.sunshower.core.security;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserService {
    User get(Identifier id);

    User findByUsername(String username);

    @PreAuthorize("hasAuthority('admin')")
    User save(User u);


    @PreAuthorize("hasAuthority('admin')")
    List<User> activeUsers();

    @PreAuthorize("hasAuthority('admin')")
    List<User> inactiveUsers();
}