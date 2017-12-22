package io.sunshower.model.core.auth;

import io.sunshower.model.core.io.File;
import io.sunshower.persistence.core.DistributableEntity;

import javax.persistence.*;

/**
 * Created by haswell on 5/22/17.
 */
@Entity
@Table(name = "TENANT_DETAILS")
public class TenantDetails extends DistributableEntity {

    @OneToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @OneToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "root_id",
            insertable = false,
            updatable = false
    )
    private File root;


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public File getRoot() {
        return root;
    }

    public void setRoot(File root) {
        this.root = root;
    }
}
