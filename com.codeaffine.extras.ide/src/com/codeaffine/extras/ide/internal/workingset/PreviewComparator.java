package com.codeaffine.extras.ide.internal.workingset;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ViewerComparator;


public class PreviewComparator extends ViewerComparator {

  private final ProjectPatternMatcher patternMatcher;

  public PreviewComparator( String pattern ) {
    patternMatcher = new ProjectPatternMatcher( pattern );
  }

  @Override
  public int category( Object element ) {
    IProject project = ( IProject )element;
    return patternMatcher.matches( project ) ? 0 : 1;
  }
}
