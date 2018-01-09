package io.sunshower.service.task;

/** Created by haswell on 2/6/17. */
public @interface Precondition {
  int order() default -1;

  Class<?> condition();
}
