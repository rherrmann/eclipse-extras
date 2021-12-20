package com.codeaffine.extras.archive.internal.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.FrameworkUtil;

public class StatusUtil {

  public static void logError(Throwable throwable) {
    IStatus status = createErrorStatus(throwable);
    StatusManager.getManager().handle(status, StatusManager.LOG);
  }

  public static void logError(String message, Throwable throwable) {
    IStatus status = createStatus(IStatus.ERROR, message, throwable);
    StatusManager.getManager().handle(status, StatusManager.LOG);
  }

  public static void log(int severity, String message) {
    IStatus status = createStatus(severity, message, null);
    StatusManager.getManager().handle(status, StatusManager.LOG);
  }

  public static void show(Throwable exception) {
    IStatus status = createErrorStatus(exception);
    StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
  }

  public static IStatus createErrorStatus(Throwable throwable) {
    return createStatus(IStatus.ERROR, throwable.getMessage(), throwable);
  }

  public static IStatus createStatus(int severity, String message, Throwable throwable) {
    return new Status(severity, getPluginId(), message, throwable);
  }

  private static String getPluginId() {
    return FrameworkUtil.getBundle(StatusUtil.class).getSymbolicName();
  }

  private StatusUtil() {
    // prevent instantiation
  }
}
