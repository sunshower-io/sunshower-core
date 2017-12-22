package io.sunshower.model.core.event;

import io.sunshower.common.Identifier;

import java.io.Serializable;
;

/**
 * Created by haswell on 2/19/17.
 */


public interface Event<T extends Serializable, U extends Serializable> {


    Identifier getId();

    T getType();

    U getCategory();


}
