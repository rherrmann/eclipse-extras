package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Widget;

public class JUnitProgressUI implements ProgressUI {
  private final JUnitProgressBar progressBar;

  public JUnitProgressUI( JUnitProgressBar progressBar ) {
    this.progressBar = progressBar;
  }

  @Override
  public void update( String text, int textAlignment, Color barColor, int selection, int maximum ) {
    progressBar.setValues( text, textAlignment, barColor, selection, maximum );
  }

  @Override
  public void setToolTipText( String toolTipText ) {
    progressBar.setToolTipText( toolTipText );
  }

  @Override
  public Widget getWidget() {
    return progressBar;
  }
}