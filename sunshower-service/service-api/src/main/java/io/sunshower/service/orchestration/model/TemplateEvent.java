package io.sunshower.service.orchestration.model;

public class TemplateEvent<T> {

    public enum Type {
        ContentSaved,
        ContentWritten,
        ContentDeleted;
    }
   
    final Type type;
   
    public TemplateEvent(final Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return type;
    } 
}
