package com.codeaffine.extras.internal.workingset;

import org.eclipse.core.resources.IProject;

interface ProjectsProvider {
  IProject[] getProjects();
}