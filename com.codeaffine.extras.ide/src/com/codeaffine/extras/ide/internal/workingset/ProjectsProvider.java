package com.codeaffine.extras.ide.internal.workingset;

import org.eclipse.core.resources.IProject;

interface ProjectsProvider {
  IProject[] getProjects();
}