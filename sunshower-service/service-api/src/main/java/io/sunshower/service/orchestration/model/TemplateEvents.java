package io.sunshower.service.orchestration.model;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.PropertyAwareObject;
import io.sunshower.service.graph.service.ContentResolver;
import io.sunshower.service.hal.core.Content;
import io.sunshower.service.hal.core.Element;

public final class TemplateEvents {

  public static TemplateEvent<ContentSavedEvent> contentSaved(
      final Content content,
      final Identifier entityId,
      final Class<?> entityType,
      final Template template,
      final ContentResolver resolver) {
    return new ContentSavedEvent(content, entityId, entityType, template, resolver);
  }

  public static TemplateEvent contentWritten(
      Content content,
      Template entity,
      String s,
      Identifier targetId,
      Class<? extends PropertyAwareObject> targetType,
      Element target) {
    return new ContentWrittenEvent(entity, s, targetId, targetType, content, target);
  }

  public static final class ContentSavedEvent extends TemplateEvent<ContentSavedEvent> {
    private final Content content;
    private final Identifier elementId;
    private final Class<?> elementType;
    private final Template owner;
    private final ContentResolver contentResolver;

    public ContentSavedEvent(
        Content content,
        Identifier elementId,
        Class<?> elementType,
        Template owner,
        ContentResolver contentResolver) {
      super(Type.ContentSaved);
      this.owner = owner;
      this.content = content;
      this.elementId = elementId;
      this.elementType = elementType;
      this.contentResolver = contentResolver;
    }

    public Identifier getElementId() {
      return elementId;
    }

    public Class<?> getElementType() {
      return elementType;
    }

    public Content getContent() {
      return content;
    }

    public Template getOwner() {
      return owner;
    }

    public ContentResolver getContentResolver() {
      return contentResolver;
    }
  }
}
