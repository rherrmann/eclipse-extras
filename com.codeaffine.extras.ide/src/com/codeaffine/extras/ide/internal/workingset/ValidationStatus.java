package com.codeaffine.extras.ide.internal.workingset;


public class ValidationStatus {

  public static enum Severity {
    NONE,
    WARNING,
    ERROR
  }

  private final Severity severity;
  private final String message;

  public ValidationStatus( Severity severity, String message ) {
    this.severity = severity;
    this.message = message;
  }

  public Severity getSeverity() {
    return severity;
  }

  public String getMessage() {
    return message;
  }
}
