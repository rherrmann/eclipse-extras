package com.codeaffine.extras.archive.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.eclipse.core.runtime.IStatus;
import org.junit.Test;

public class StatusUtilTest {

  @Test
  public void testCreateStatus() {
    String message = "message";
    Throwable exception = new Throwable(message);

    IStatus status = StatusUtil.createErrorStatus(exception);

    assertEquals(message, status.getMessage());
    assertSame(exception, status.getException());
    assertEquals("com.codeaffine.extras.archive", status.getPlugin());
  }

  @Test
  public void testCreateStatusWithMessage() {
    String message = "message";
    Throwable exception = new Throwable();

    IStatus status = StatusUtil.createStatus(IStatus.ERROR, message, exception);

    assertEquals(message, status.getMessage());
    assertSame(exception, status.getException());
    assertEquals("com.codeaffine.extras.archive", status.getPlugin());
  }
}
