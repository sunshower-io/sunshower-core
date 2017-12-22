package io.sunshower.service.task;

import java.lang.annotation.*;

/**
 * Created by haswell on 2/4/17.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transformation {
    Class<?> value();
}
