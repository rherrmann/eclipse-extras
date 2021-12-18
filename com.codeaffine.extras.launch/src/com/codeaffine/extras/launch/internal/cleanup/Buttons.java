package com.codeaffine.extras.launch.internal.cleanup;

import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;

class Buttons {

  static int computePreferredButtonWidth(Button button) {
    int defaultButtonWidth = getDefaultButtonWidth();
    Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    return Math.max(defaultButtonWidth, minSize.x);
  }

  private static int getDefaultButtonWidth() {
    return LayoutConstants.getMinButtonSize().x;
  }

  private Buttons() {}
}
