package com.codeaffine.extras.launch.internal.dialog;

import org.eclipse.ui.dialogs.SearchPattern;


public class LaunchConfigSearchPattern extends SearchPattern {

  public LaunchConfigSearchPattern() {
    super(RULE_BLANK_MATCH | RULE_PATTERN_MATCH | RULE_CAMELCASE_MATCH);
  }

  @Override
  public void setPattern(String pattern) {
    String adjustedPattern = pattern;
    if (adjustedPattern.endsWith(" ")) {
      adjustedPattern = adjustedPattern + "*";
    }
    super.setPattern(adjustedPattern);
    if (getMatchRule() == RULE_PATTERN_MATCH || getMatchRule() == RULE_EXACT_MATCH) {
      super.setPattern(prependPattern(adjustedPattern));
    }
  }

  private static String prependPattern(String pattern) {
    return !pattern.isEmpty() && !pattern.startsWith("*") ? "*" + pattern : pattern;
  }

}
