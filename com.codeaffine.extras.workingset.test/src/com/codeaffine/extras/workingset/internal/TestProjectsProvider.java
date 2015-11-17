package com.codeaffine.extras.workingset.internal;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;

public class TestProjectsProvider implements ProjectsProvider {
  private final Collection<IProject> projects;

  public TestProjectsProvider() {
    projects = new LinkedList<>();
  }

  public void addProject( IProject project ) {
    projects.add( project );
  }

  public void removeProject( IProject project ) {
    projects.remove( project );
  }

  @Override
  public IProject[] getProjects() {
    return projects.toArray( new IProject[ projects.size() ] );
  }
}