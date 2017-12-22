package io.sunshower.service.task;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by haswell on 2/6/17.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Preconditions {
    Precondition[] value() default {};
}
