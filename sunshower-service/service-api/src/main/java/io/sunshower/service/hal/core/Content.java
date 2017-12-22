package io.sunshower.service.hal.core;

import io.sunshower.model.core.io.File;
import io.sunshower.service.model.PropertyAwareObject;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by haswell on 10/13/17.
 */
@XmlRootElement(name = "content")
public class Content extends PropertyAwareObject<Content> {

    public static final String DEFAULT = "default";
    
    @XmlElement
    private File file;
   
    @XmlAttribute
    private Integer order;
    
    @XmlAttribute(name = "media-type")
    private String mediaType;

    @XmlAttribute
    private String name;
    
    @XmlAttribute(name = "content-type")
    private ContentType type;
    
    @XmlAttribute(name = "ref")
    private String reference;
    
    
    public Content() {
        super(Content.class);
    }
    
    

    public Content(ContentType type, String reference) {
        this();
        this.type = type;
        this.reference = reference;
    }

    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getDEFAULT() {
        return DEFAULT;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ContentType getContentType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    public String getMediaType() {
        return mediaType;
    }

}
