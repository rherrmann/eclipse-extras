package com.codeaffine.extras.workingset.internal;

import org.eclipse.core.resources.IProject;

interface ProjectsProvider {
  IProject[] getProjects();
}