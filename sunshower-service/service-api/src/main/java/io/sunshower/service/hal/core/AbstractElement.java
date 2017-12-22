package io.sunshower.service.hal.core;

import io.sunshower.common.Identifier;
import io.sunshower.common.rs.ClassAdapter;
import io.sunshower.common.rs.IdentifierConverter;
import io.sunshower.common.rs.MapAdapter;
import io.sunshower.common.rs.TypeAttributeClassExtractor;
import io.sunshower.model.core.auth.ProtectedDistributableEntity;
import io.sunshower.service.model.Property;
import io.sunshower.service.model.PropertyAwareObject;
import org.eclipse.persistence.oxm.annotations.XmlClassExtractor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * Created by haswell on 5/22/17.
 */

@XmlClassExtractor(TypeAttributeClassExtractor.class)
public class AbstractElement<T extends AbstractElement<T>>
        extends PropertyAwareObject<T>
        implements Element, Styleable, Iterable<Map.Entry<String, Object>> {

    /**
     *
     */
    @XmlElement
    private Layout layout;

    @XmlAttribute
    private String name;


    @XmlElement(name = "style")
    @XmlJavaTypeAdapter(MapAdapter.class)
    private Stylesheet style;


    @XmlElement(name = "content")
    @XmlElementWrapper(name = "contents")
    private Set<Content> contents;


    /**
     *
     */
    @XmlAttribute(name = "element-type")
    private ElementType elementType;
    
    @XmlElement(name = "element-properties")
    private Properties elementProperties;


    public AbstractElement() {
        super();
        this.name = getId().toString();
    }

    public AbstractElement(String name) {
        this();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public AbstractElement(Class<T> type) {
        super(type);
    }

    public void setId(Identifier id) {
        super.setId(id);
    }


    public void setProperties(Collection<? extends Property<?, ?>> properties) {
        for (Property<?, ?> p : properties) {
            addProperty(p);
        }
    }


    @Override
    public Layout getLayout() {
        if (layout == null) {
            layout = new Layout(this);
        }
        return layout;
    }

    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;

    }


    public Stylesheet getStyle() {
        return style;
    }

    public void setStyle(Stylesheet style) {
        this.style = style;
    }

    @Override
    public Set<Content> getContents() {
        return contents == null ? Collections.emptySet() : contents;
    }

    @Override
    public void addContent(Content content) {
        if (contents == null) {
            contents = new LinkedHashSet<>();
        }
        if (content != null) {
            contents.add(content);
        }
    }

    @Override
    public void addElementProperty(String key, String value) {
        if(elementProperties == null) {
            elementProperties = new Properties();
        }
        elementProperties.put(key, value);
    }
    
    
    public String getElementProperty(String key) {
        if(elementProperties == null) {
            return null;
        }
        return elementProperties.getProperty(key);
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }


    protected void setDefaults() {
        setLayout(new Layout(this));
        setCreated(new Date());
        setModified(new Date());
    }

    @Override
    public String getStyle(String key) {
        return checkStyles().get(key);
    }

    @Override
    public void setStyle(String key, String value) {
        checkStyles().set(key, value);
    }

    private Stylesheet checkStyles() {
        if (style == null) {
            style = new Stylesheet();
        }
        return style;
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        if(elementProperties != null && elementProperties.getProperties() != null) {
            return elementProperties.getProperties().entrySet().iterator();
        }
        return Collections.<String, Object>emptyMap().entrySet().iterator();
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<String, Object>> action) {
        if(elementProperties != null && elementProperties.getProperties() != null) {
            elementProperties.getProperties().forEach((v, e) -> {
                
            });
        }
    }

    @Override
    public Spliterator<Map.Entry<String, Object>> spliterator() {
        if(elementProperties != null && elementProperties.getProperties() != null) {
            return Spliterators.spliterator(
                    elementProperties.getProperties().entrySet(),
                    Spliterator.ORDERED | Spliterator.SIZED
            );
        }
        return Collections.<String, Object>emptyMap().entrySet().spliterator();
    }
}
