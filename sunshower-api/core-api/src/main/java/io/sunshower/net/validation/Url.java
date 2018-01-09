package io.sunshower.net.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/** Created by haswell on 5/22/17. */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = UrlValidator.class)
@Documented
public @interface Url {

  String message() default "URL is invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
