package io.sunshower.inject;


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

/**
 * Created by haswell on 3/5/17.
 */
public class FieldNameResolver implements NameResolver {

    @Override
    public String resolve(AccessibleObject target) {
        return ((Field) target).getName();
    }
}
