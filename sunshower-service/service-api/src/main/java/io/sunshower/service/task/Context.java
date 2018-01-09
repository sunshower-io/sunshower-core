package io.sunshower.service.task;

import java.lang.annotation.*;

/** Created by haswell on 1/31/17. */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Context {

  String name() default "";
}
