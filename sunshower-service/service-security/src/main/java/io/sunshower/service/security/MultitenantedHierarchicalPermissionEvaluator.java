package io.sunshower.service.security;

import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by haswell on 5/9/17.
 */
public class MultitenantedHierarchicalPermissionEvaluator extends AclPermissionEvaluator {

    public MultitenantedHierarchicalPermissionEvaluator(AclService aclService) {
        super(aclService);
    }


    @Override
    public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {

        if (isAdmin(authentication)) return true;
        return super.hasPermission(authentication, domainObject, permission);
    }


    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if(isAdmin(authentication)) {
            return true;
        }
        return super.hasPermission(authentication, targetId, targetType, permission);
    }


    private boolean isAdmin(Authentication authentication) {
        final String role = DefaultRoles.SITE_ADMINISTRATOR.authority();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for(GrantedAuthority authority : authorities) {
            if(role.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
