package io.sunshower.service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haswell on 2/7/17.
 */
public interface Repository<T, U extends Serializable> {

    /**
     * @param id
     * @return
     */
    T get(U id);

    /**
     * @param entity
     * @return
     */
    U save(T entity);

    /**
     * @return
     */
    List<T> list();

    /**
     * @param id
     * @return
     */

    T delete(U id);
}
