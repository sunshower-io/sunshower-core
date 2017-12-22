package io.sunshower.service.security;

import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by haswell on 5/9/17.
 */
public class MultitenantedAclAuthorizationStrategy extends AclAuthorizationStrategyImpl implements AclAuthorizationStrategy {

    public MultitenantedAclAuthorizationStrategy(GrantedAuthority root)  {
        super(root);
    }

}
