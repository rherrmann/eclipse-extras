package com.codeaffine.extras.test.util;


public class ParameterInfo {

  private final String id;
  private final String value;

  public ParameterInfo( String id, String value ) {
    this.id = id;
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public String getValue() {
    return value;
  }
}
