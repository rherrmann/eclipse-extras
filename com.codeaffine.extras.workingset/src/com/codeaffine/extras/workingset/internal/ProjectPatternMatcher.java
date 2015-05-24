package com.codeaffine.extras.workingset.internal;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.resources.IProject;


public class ProjectPatternMatcher {

  private final Pattern pattern;

  public ProjectPatternMatcher( String regex ) {
    pattern = compilePattern( regex );
  }

  public boolean isPatternValid() {
    return pattern != null;
  }

  public boolean matches( IProject project ) {
    return pattern != null && pattern.matcher( project.getName() ).matches();
  }

  private static Pattern compilePattern( String regex ) {
    Pattern result;
    try {
      result = Pattern.compile( regex );
    } catch( PatternSyntaxException ignore ) {
      result = null;
    }
    return result;
  }

}
