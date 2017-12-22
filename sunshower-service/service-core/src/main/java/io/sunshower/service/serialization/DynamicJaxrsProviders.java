package io.sunshower.service.serialization;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DynamicJaxrsProviders implements javax.ws.rs.ext.Providers {


    private final Map<Class<?>, ExceptionMapper<?>> exceptionMappers;
    private final Map<Class<?>, MessageBodyReader<?>> messageBodyReaders;
    private final Map<Class<?>, MessageBodyWriter<?>> messageBodyWriters;
    
    
    public DynamicJaxrsProviders() {
        exceptionMappers = new HashMap<>();
        messageBodyReaders = new HashMap<>();
        messageBodyWriters = new HashMap<>();
    }
    
    @Override
    public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return null;
    }

    @Override
    public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return null;
    }

    @Override
    public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type) {
        return null;
    }

    @Override
    public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType) {
        return null;
    }
}
