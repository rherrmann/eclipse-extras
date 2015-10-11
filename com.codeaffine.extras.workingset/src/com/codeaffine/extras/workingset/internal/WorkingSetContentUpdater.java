package com.codeaffine.extras.workingset.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;

class WorkingSetContentUpdater {

  private final IWorkingSet workingSet;
  private final ProjectsProvider projectsProvider;
  private final ProjectPatternMatcher projectPatternMatcher;

  WorkingSetContentUpdater( IWorkingSet workingSet ) {
    this( workingSet, new WorkspaceProjectsProvider() );
  }

  WorkingSetContentUpdater( IWorkingSet workingSet, ProjectsProvider projectsProvider ) {
    this.workingSet = workingSet;
    this.projectsProvider = projectsProvider;
    this.projectPatternMatcher = new ProjectPatternMatcher( workingSet.getName() );
  }

  void updateElements() {
    Set<IProject> matchingProjects = new HashSet<>();
    for( IProject project : projectsProvider.getProjects() ) {
      if( projectPatternMatcher.matches( project ) ) {
        matchingProjects.add( project );
      }
    }
    if( elementsChanged( matchingProjects ) ) {
      workingSet.setElements( matchingProjects.toArray( new IProject[ matchingProjects.size() ] ) );
    }
  }

  private boolean elementsChanged( Set<IProject> newElements ) {
    IAdaptable[] oldElements = workingSet.getElements();
    if( oldElements.length != newElements.size() ) {
      return true;
    }
    for( IAdaptable oldElement : oldElements ) {
      if( !newElements.contains( oldElement ) ) {
        return true;
      }
    }
    return false;
  }
}