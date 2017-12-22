package io.sunshower.service.task;


import java.lang.annotation.*;

/**
 * Created by haswell on 2/3/17.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    String value() default "";
    String expression() default "";
}
