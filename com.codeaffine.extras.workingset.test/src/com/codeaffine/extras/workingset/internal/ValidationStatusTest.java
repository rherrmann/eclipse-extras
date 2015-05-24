package com.codeaffine.extras.workingset.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.extras.workingset.internal.ValidationStatus;
import com.codeaffine.extras.workingset.internal.ValidationStatus.Severity;

public class ValidationStatusTest {

  @Test
  public void testConstructor() {
    Severity severity = Severity.ERROR;
    String message = "message";
    ValidationStatus validationStatus = new ValidationStatus( severity, message );

    assertThat( validationStatus.getSeverity() ).isEqualTo( severity );
    assertThat( validationStatus.getMessage() ).isEqualTo( message );
  }
}
