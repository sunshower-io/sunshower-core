package io.sunshower.service.task;

/** Created by haswell on 1/31/17. */

import java.lang.annotation.*;

/** Created by haswell on 1/31/17. */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {

  String key();

  Class<?> definition();
}
