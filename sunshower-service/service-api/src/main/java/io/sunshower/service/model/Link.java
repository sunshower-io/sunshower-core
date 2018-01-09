package io.sunshower.service.model;

public interface Link<T, U> {

  T getSource();

  U getTarget();

  LinkageMode getLinkageMode();

  RelationshipType getRelationshipType();
}
