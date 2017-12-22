package io.sunshower.service.hal.core;

import io.sunshower.common.Identifier;

public class ContentCoordinate {

    private final Identifier nodeId; 
    private final Identifier contentId;
    private final Identifier templateId;

    public ContentCoordinate(
            Identifier nodeId, 
            Identifier contentId, 
            Identifier templateId
    ) {
        this.nodeId = nodeId;
        this.contentId = contentId;
        this.templateId = templateId;
    }

    public Identifier getNodeId() {
        return nodeId;
    }

    public Identifier getContentId() {
        return contentId;
    }

    public Identifier getTemplateId() {
        return templateId;
    }
}
