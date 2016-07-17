package com.codeaffine.extras.imageviewer.internal;

import org.eclipse.swt.layout.FillLayout;

class FillLayoutFactory {

  static FillLayout newFillLayout( int margin ) {
    FillLayout layout = new FillLayout();
    layout.marginHeight = margin;
    layout.marginWidth = margin;
    return layout;
  }

  private FillLayoutFactory() { }
}
