package io.sunshower.service.hal.core;

import io.sunshower.common.Identifier;
import io.sunshower.persistence.core.Persistable;

import java.util.Set;
import java.util.UUID;

/**
 * Created by haswell on 5/22/17.
 */
public interface Element extends Persistable<Identifier> {


    Layout getLayout();

    void setLayout(Layout layout);

    Set<Content> getContents();

    void addContent(Content content);
    
    void addElementProperty(String key, String value);

}
