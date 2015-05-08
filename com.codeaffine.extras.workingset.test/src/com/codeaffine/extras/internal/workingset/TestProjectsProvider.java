package com.codeaffine.extras.internal.workingset;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;

import com.codeaffine.extras.internal.workingset.ProjectsProvider;
import com.google.common.collect.Iterables;

public class TestProjectsProvider implements ProjectsProvider {
  private final Collection<IProject> projects;

  public TestProjectsProvider() {
    projects = new LinkedList<IProject>();
  }

  public void addProject( IProject project ) {
    projects.add( project );
  }

  public void removeProject( IProject project ) {
    projects.remove( project );
  }

  @Override
  public IProject[] getProjects() {
    return Iterables.toArray( projects, IProject.class );
  }
}