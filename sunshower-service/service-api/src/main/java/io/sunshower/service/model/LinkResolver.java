package io.sunshower.service.model;

public interface LinkResolver<T, U, V> {
    
    Link<T, U> getLink();
    
    
    V resolve(Class<V> v);
}
