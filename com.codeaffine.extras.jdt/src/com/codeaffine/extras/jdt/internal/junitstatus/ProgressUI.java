package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Widget;

public interface ProgressUI {

  void update( String text, int textAlignment, Color barColor, int selection, int maximum );
  Widget getWidget();
}
