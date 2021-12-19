package com.codeaffine.extras.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public class UIThreadSynchronizer {

  public void asyncExec(Widget contextWidget, Runnable runnable) {
    new UIThreadRunner(contextWidget).asyncExec(runnable);
  }

  public void syncExec(Widget contextWidget, Runnable runnable) {
    new UIThreadRunner(contextWidget).syncExec(runnable);
  }

  private static class UIThreadRunner {
    private final Widget contextWidget;

    UIThreadRunner(Widget contextWidget) {
      this.contextWidget = contextWidget;
    }

    void asyncExec(Runnable runnable) {
      Display display = getDisplay();
      if (display != null) {
        display.asyncExec(new GuardedRunnable(contextWidget, runnable));
      }
    }

    void syncExec(Runnable runnable) {
      Display display = getDisplay();
      if (display != null) {
        display.syncExec(new GuardedRunnable(contextWidget, runnable));
      }
    }

    private Display getDisplay() {
      Display result = null;
      if (!contextWidget.isDisposed()) {
        result = safeGetDisplay();
      }
      return result;
    }

    private Display safeGetDisplay() {
      Display result = null;
      try {
        result = contextWidget.getDisplay();
      } catch (SWTException exception) {
        handleSWTException(exception);
      }
      return result;
    }

    private static void handleSWTException(SWTException exception) {
      if (exception.code != SWT.ERROR_WIDGET_DISPOSED) {
        throw exception;
      }
    }
  }

  private static class GuardedRunnable implements Runnable {
    private final Widget contextWidget;
    private final Runnable runnable;

    GuardedRunnable(Widget contextWidget, Runnable runnable) {
      this.contextWidget = contextWidget;
      this.runnable = runnable;
    }

    @Override
    public void run() {
      if (!contextWidget.isDisposed()) {
        runnable.run();
      }
    }
  }

}
