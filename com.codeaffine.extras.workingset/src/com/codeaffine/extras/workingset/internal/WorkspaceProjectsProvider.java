package com.codeaffine.extras.workingset.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class WorkspaceProjectsProvider implements ProjectsProvider {
  @Override
  public IProject[] getProjects() {
    return ResourcesPlugin.getWorkspace().getRoot().getProjects();
  }
}