package io.sunshower.service.model;

import io.sunshower.persistence.core.DistributableEntity;

import javax.persistence.*;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractEntityLink<
            T extends DistributableEntity, 
            U extends DistributableEntity
        > extends DistributableEntity implements Link<T, U> {
   
    @OneToOne(
            fetch = FetchType.LAZY
    )
    private T source;

    @OneToOne(
            fetch = FetchType.LAZY
    )
    private U target;
  
    @Enumerated
    @Column(name="mode")
    private LinkageMode mode;
    
  
    @Enumerated
    @Column(name = "type")
    private RelationshipType type;
    
    
    
    public AbstractEntityLink(
            T source, 
            U target,
            LinkageMode mode,
            RelationshipType type
    ) {
        setSource(source);
        setTarget(target);
        setMode(mode);
        setType(type);
    }
    
    @Override
    public T getSource() {
        return source;
    }

    @Override
    public U getTarget() {
        return target;
    }

    @Override
    public LinkageMode getLinkageMode() {
        return mode;
    }

    @Override
    public RelationshipType getRelationshipType() {
        return type;
    }

    protected void setSource(T source) {
        this.source = source;
    }
    
    protected void setTarget(U target) {
        this.target = target;
    }

    protected void setMode(LinkageMode mode) {
        this.mode = mode;
    }
    
    protected void setType(RelationshipType type) {
        this.type = type;
    }
    
    
}
