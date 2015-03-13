package com.codeaffine.extras.ide.internal.workingset;

import static com.codeaffine.extras.ide.internal.workingset.ValidationStatus.Severity.ERROR;

import org.eclipse.core.resources.IProject;

import com.codeaffine.extras.ide.internal.workingset.ValidationStatus.Severity;


public class Validator {

  static final String MSG_NAME_EMPTY = "Please enter a name for the working set";
  static final String MSG_PATTERN_EMPTY = "Please enter a regular expression pattern";
  static final String MSG_PATTERN_INVALID = "The regular expression is not valid";
  static final String MSG_JDT_RESTRICTION = "Due to a restriction in the Java Tools, working sets that are initially empty are not shown in the Package Explorer.";

  private final JdtFeature jdtFeature;
  private final ProjectsProvider projectsProvider;

  public Validator( ProjectsProvider projectsProvider, JdtFeature jdtFeature ) {
    this.projectsProvider = projectsProvider;
    this.jdtFeature = jdtFeature;
  }

  public ValidationStatus validate( String name, String pattern ) {
    if( name.isEmpty() ) {
      return new ValidationStatus( ERROR, MSG_NAME_EMPTY );
    }
    if( pattern.isEmpty() ) {
      return new ValidationStatus( ERROR, MSG_PATTERN_EMPTY );
    }
    if( !isPatternValid( pattern ) ) {
      return new ValidationStatus( ERROR, MSG_PATTERN_INVALID );
    }
    if( !pattern.isEmpty() && jdtFeature.isInstalled() && !patternMatchesAnyProject( pattern ) ) {
      return new ValidationStatus( Severity.WARNING, MSG_JDT_RESTRICTION );
    }
    return new ValidationStatus( Severity.NONE, "" );
  }

  private boolean patternMatchesAnyProject( String pattern ) {
    boolean result = false;
    ProjectPatternMatcher projectPatternMatcher = new ProjectPatternMatcher( pattern );
    IProject[] projects = projectsProvider.getProjects();
    for( int i = 0; !result && i < projects.length; i++ ) {
      IProject project = projects[ i ];
      if( projectPatternMatcher.matches( project ) ) {
        result = true;
      }
    }
    return result;
  }

  private static boolean isPatternValid( String pattern ) {
    return new ProjectPatternMatcher( pattern ).isPatternValid();
  }

}
