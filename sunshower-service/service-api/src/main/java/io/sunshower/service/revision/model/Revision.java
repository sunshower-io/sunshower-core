package io.sunshower.service.revision.model;

import io.sunshower.model.core.auth.ProtectedDistributableEntity;
import io.sunshower.persistence.core.converters.ClassConverter;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;

/**
 * Created by haswell on 5/22/17.
 */

@Entity
public class Revision extends ProtectedDistributableEntity {


    @Basic
    @Column(name = "type")
    @Convert(converter = ClassConverter.class)
    private Class<?> type;



    @Basic
    @Column(name = "revision")
    private String revision;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }


    @Override
    public String toString() {
        return "Revision{" +
                "type=" + type +
                ", revision='" + revision + '\'' +
                '}';
    }
}
