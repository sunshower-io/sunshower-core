package io.sunshower.service.repository;

import java.util.List;

/** Created by haswell on 5/16/17. */
public interface EntityRepository<ID, E> {

  E save(E entity);

  /**
   * @param entity
   * @return
   */
  E create(E entity);

  /**
   * @param entity
   * @return
   */
  E update(E entity);

  /** @return */
  List<E> list();

  /**
   * @param id
   * @return
   */
  E get(ID id);

  /**
   * @param id
   * @return
   */
  E delete(ID id);
}
