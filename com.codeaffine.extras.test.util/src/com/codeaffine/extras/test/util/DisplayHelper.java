package com.codeaffine.extras.test.util;

import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.rules.ExternalResource;

public class DisplayHelper extends ExternalResource {

  private final Collection<Shell> capturedShells;
  private final Collection<Image> images;
  private Display display;
  private boolean displayOwner;

  public DisplayHelper() {
    capturedShells = new ArrayList<>();
    images = new ArrayList<>();
  }

  @Override
  protected void before() throws Throwable {
    display = Display.getCurrent();
    if (display == null) {
      display = new Display();
      displayOwner = true;
    }
    capturedShells.addAll(asList(captureShells()));
  }

  @Override
  protected void after() {
    flushPendingEvents();
    disposeNewShells();
    disposeImages();
    disposeDisplay();
  }

  public Display getDisplay() {
    return display;
  }

  public void flushPendingEvents() {
    for (int i = 0; i < getRepeatCount(); i++) {
      while (display != null && !display.isDisposed() && display.readAndDispatch()) { }
    }
  }

  public Shell[] getNewShells() {
    return Stream.of(captureShells()).filter(shell -> !capturedShells.contains(shell)).toArray(Shell[]::new);
  }

  public Shell createShell() {
    return createShell(SWT.NONE);
  }

  public Shell createShell(int style) {
    return new Shell(display, style);
  }

  public Image createImage(int width, int height) {
    Image result = new Image(display, width, height);
    images.add(result);
    return result;
  }

  private void disposeNewShells() {
    for (Shell shell : getNewShells()) {
      shell.dispose();
    }
  }

  private void disposeImages() {
    for (Image image : images) {
      image.dispose();
    }
  }

  private Shell[] captureShells() {
    return display.getShells();
  }

  private void disposeDisplay() {
    if (displayOwner) {
      display.dispose();
    }
  }

  // Workaround for a strange behavior on macOS: a single call to Display::readAndDispatch does not seem sufficient.
  // The number of retries was found by trial and error.
  private static int getRepeatCount() {
    return "cocoa".equals(SWT.getPlatform()) ? 8 : 1;
  }
}
