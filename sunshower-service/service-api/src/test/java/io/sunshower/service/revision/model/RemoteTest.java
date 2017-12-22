package io.sunshower.service.revision.model;

import io.sunshower.service.PersistTestCase;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class RemoteTest extends PersistTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void ensureRemoteCanBeSavedWithValidNameAndUrl() {
        final Remote remote = new Remote();
        remote.setUri("/frap/adap/wap");
        remote.setName("frap");
        entityManager.persist(remote);
        entityManager.flush();
    }


    @Test
    public void ensureRelativeUrlWorks() {
        final Remote remote = new Remote();
        remote.setUri("/frap/adap");
        remote.setName("frap");
        entityManager.persist(remote);
    }

}