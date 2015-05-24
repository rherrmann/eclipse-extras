package com.codeaffine.extras.workingset.internal;

import static com.codeaffine.extras.workingset.internal.ValidationStatus.Severity.ERROR;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkingSet;

import com.codeaffine.extras.workingset.internal.ValidationStatus.Severity;


public class Validator {

  static final String MSG_NAME_EMPTY = "Please enter a name for the working set";
  static final String MSG_NAME_EXISTS = "A working set with the same name already exists";
  static final String MSG_PATTERN_EMPTY = "Please enter a regular expression pattern";
  static final String MSG_PATTERN_INVALID = "The regular expression is not valid";
  static final String MSG_JDT_RESTRICTION = "Due to a restriction in the Java Tools, working sets that are initially empty are not shown in the Package Explorer.";

  private final WorkingSetsProvider workingSetsProvider;
  private final ProjectsProvider projectsProvider;
  private final JdtFeature jdtFeature;

  public Validator( ProjectsProvider projectsProvider, JdtFeature jdtFeature ) {
    this( new WorbenchWorkingSetsProvider(), projectsProvider, jdtFeature );
  }

  public Validator( WorkingSetsProvider workingSetsProvider,
                    ProjectsProvider projectsProvider,
                    JdtFeature jdtFeature )
  {
    this.workingSetsProvider = workingSetsProvider;
    this.projectsProvider = projectsProvider;
    this.jdtFeature = jdtFeature;
  }

  public ValidationStatus validate( IWorkingSet editedWorkingSet, String name, String pattern ) {
    if( name.isEmpty() ) {
      return new ValidationStatus( ERROR, MSG_NAME_EMPTY );
    }
    if( pattern.isEmpty() ) {
      return new ValidationStatus( ERROR, MSG_PATTERN_EMPTY );
    }
    if( !isPatternValid( pattern ) ) {
      return new ValidationStatus( ERROR, MSG_PATTERN_INVALID );
    }
    if( nameExists( editedWorkingSet, name ) ) {
      return new ValidationStatus( Severity.WARNING, MSG_NAME_EXISTS );
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

  private boolean nameExists( IWorkingSet editedWorkingSet, String name ) {
    IWorkingSet[] workingSets = workingSetsProvider.getWorkingSets();
    for( IWorkingSet workingSet : workingSets ) {
      if( !workingSet.equals( editedWorkingSet ) && name.equals( workingSet.getLabel() ) ) {
        return true;
      }
    }
    return false;
  }

}
