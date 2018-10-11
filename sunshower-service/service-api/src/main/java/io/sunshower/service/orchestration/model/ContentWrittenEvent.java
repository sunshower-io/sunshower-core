package io.sunshower.service.orchestration.model;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.PropertyAwareObject;
import io.sunshower.service.hal.core.Content;
import io.sunshower.service.hal.core.Element;

public class ContentWrittenEvent extends TemplateEvent {
  private final Content content;
  private final String data;
  private final Identifier targetId;
  private final Template entity;
  private final Class<? extends PropertyAwareObject> targetType;
  private final Element target;

  public ContentWrittenEvent(
      Template entity,
      String s,
      Identifier targetId,
      Class<? extends PropertyAwareObject> targetType,
      Content content,
      Element target) {
    super(Type.ContentWritten);
    this.data = s;
    this.target = target;
    this.entity = entity;
    this.targetId = targetId;
    this.content = content;
    this.targetType = targetType;
  }

  public Element getTarget() {
    return target;
  }

  public Content getContent() {
    return content;
  }

  public String getData() {
    return data;
  }

  public Template getEntity() {
    return entity;
  }

  public Identifier getTargetId() {
    return targetId;
  }

  public Class<? extends PropertyAwareObject> getTargetType() {
    return targetType;
  }
}
