package io.sunshower.service;

/** Created by haswell on 2/17/17. */
public interface Joinable<T> {

  /**
   * Wait for some event to happen
   *
   * @throws InterruptedException
   */
  T join() throws InterruptedException;
}
