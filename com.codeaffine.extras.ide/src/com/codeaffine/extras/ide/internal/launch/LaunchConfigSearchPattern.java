package com.codeaffine.extras.ide.internal.launch;

import org.eclipse.ui.dialogs.SearchPattern;


public class LaunchConfigSearchPattern extends SearchPattern {

  @Override
  public void setPattern( String stringPattern ) {
    super.setPattern( prependPattern( stringPattern ) );
  }

  private static String prependPattern( String pattern ) {
    return !pattern.isEmpty() && !pattern.startsWith( "*" ) ? "*" + pattern : pattern;
  }
}
