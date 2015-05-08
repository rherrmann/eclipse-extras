package com.codeaffine.extras.internal.workingset;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Sets.newHashSet;

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
    Set<IProject> matchingProjects = newHashSet();
    for( IProject project : projectsProvider.getProjects() ) {
      if( projectPatternMatcher.matches( project ) ) {
        matchingProjects.add( project );
      }
    }
    if( elementsChanged( matchingProjects ) ) {
      workingSet.setElements( toArray( matchingProjects, IProject.class ) );
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