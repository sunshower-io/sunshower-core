package io.sunshower.inject;

import java.lang.reflect.Field;

/**
 * Created by haswell on 3/5/17.
 */
public class InjectionException extends RuntimeException {

    private final Class<?> current;
    private final Class<?> target;
    private final Object instance;
    private final Field field;
    private final Object binding;

    public <U> InjectionException(
            Class<?> current, 
            Class<?> target, 
            Object instance, 
            Field field, 
            Object binding, 
            IllegalAccessException e
    ) {
        super(e);
        this.current = current;
        this.target = target;
        this.instance = instance;
        this.field = field;
        this.binding = binding;
    }

    public Class<?> getCurrent() {
        return current;
    }

    public Class<?> getTarget() {
        return target;
    }

    public Object getInstance() {
        return instance;
    }

    public Field getField() {
        return field;
    }

    public Object getBinding() {
        return binding;
    }
}
