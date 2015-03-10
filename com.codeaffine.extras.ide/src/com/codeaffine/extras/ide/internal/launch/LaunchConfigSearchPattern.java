package com.codeaffine.extras.ide.internal.launch;

import org.eclipse.ui.dialogs.SearchPattern;


public class LaunchConfigSearchPattern extends SearchPattern {

  public LaunchConfigSearchPattern() {
    super( RULE_PATTERN_MATCH | RULE_CAMELCASE_MATCH );
  }

  @Override
  public void setPattern( String stringPattern ) {
    super.setPattern( stringPattern );
    if( getMatchRule() == RULE_PATTERN_MATCH || getMatchRule() == RULE_EXACT_MATCH ) {
      super.setPattern( prependPattern( stringPattern ) );
    }
  }

  private static String prependPattern( String pattern ) {
    return !pattern.isEmpty() && !pattern.startsWith( "*" ) ? "*" + pattern : pattern;
  }

}
