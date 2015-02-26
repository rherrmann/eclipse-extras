package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.swt.graphics.Color;

public interface ProgressUI {

  void update( String text, int textAlignment, Color barColor, int selection, int maximum );
  void setToolTipText( String toolTipText );
}
