package io.sunshower.service;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by haswell on 2/17/17.
 *
 * <p>Indicates that a noun or a verb is not remotable and cannot be made to be
 */
@Inherited
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface Local {}
