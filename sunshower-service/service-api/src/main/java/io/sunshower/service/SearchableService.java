package io.sunshower.service;

import java.util.List;

/**
 * Created by haswell on 4/12/17.
 */
public interface SearchableService<T> {
    List<T> search(T exemplar);
}
