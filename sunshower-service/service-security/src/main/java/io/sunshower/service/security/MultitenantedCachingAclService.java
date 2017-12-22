package io.sunshower.service.security;

import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;

import javax.sql.DataSource;

/**
 * Created by haswell on 5/9/17.
 */
public class MultitenantedCachingAclService extends UUIDJdbcMutableAclService {

    public MultitenantedCachingAclService(
            DataSource dataSource,
            LookupStrategy lookupStrategy,
            AclCache cache

    ) {
        super(dataSource, lookupStrategy, cache);
    }
}
