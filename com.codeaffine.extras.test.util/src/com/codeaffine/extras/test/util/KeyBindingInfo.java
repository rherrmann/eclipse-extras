package com.codeaffine.extras.test.util;

public class KeyBindingInfo {
  private String schemeId;
  private String commandId;
  private String contextId;
  private String platform;
  private ParameterInfo[] parameters;

  public KeyBindingInfo() {
    parameters = new ParameterInfo[ 0 ];
  }

  public String getSchemeId() {
    return schemeId;
  }

  void setSchemeId( String schemeId ) {
    this.schemeId = schemeId;
  }

  public String getCommandId() {
    return commandId;
  }

  void setCommandId( String commandId ) {
    this.commandId = commandId;
  }

  public String getContextId() {
    return contextId;
  }

  void setContextId( String contextId ) {
    this.contextId = contextId;
  }

  public String getPlatform() {
    return platform;
  }

  void setPlatform( String platform ) {
    this.platform = platform;
  }

  void setParameters( ParameterInfo[] parameters ) {
    this.parameters = parameters;
  }

  public ParameterInfo[] getParameters() {
    return parameters;
  }
}