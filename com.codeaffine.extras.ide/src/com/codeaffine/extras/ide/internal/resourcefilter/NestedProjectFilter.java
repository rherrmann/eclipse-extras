package com.codeaffine.extras.ide.internal.resourcefilter;

import static java.util.Arrays.asList;
import static org.eclipse.core.resources.IResource.PROJECT;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.filtermatchers.AbstractFileInfoMatcher;
import org.eclipse.core.runtime.Path;


public class NestedProjectFilter extends AbstractFileInfoMatcher {

  public static final String ID = "com.codeaffine.extras.ide.internal.resourcefilter.NestedProjectFilter";

  private static final RecursionGuard recursionGuard = new RecursionGuard();

  private IProject project;

  @Override
  public void initialize(IProject project, Object arguments) {
    this.project = project;
  }

  @Override
  public boolean matches(IContainer parent, IFileInfo fileInfo) {
    if (!fileInfo.isDirectory() || recursionGuard.isInUse(project)) {
      return false;
    }
    recursionGuard.enter(project);
    try {
      return findContainer(parent, fileInfo.getName()).stream()
          .anyMatch(container -> container.getType() == PROJECT && !container.equals(project));
    } finally {
      recursionGuard.leave(project);
    }
  }

  private Collection<IContainer> findContainer(IContainer parent, String folderName) {
    URI locationURI = parent.getFolder(new Path(folderName)).getLocationURI();
    return locationURI == null ? Collections.emptyList() : findContainers(locationURI);
  }

  private Collection<IContainer> findContainers(URI locationURI) {
    return asList(project.getWorkspace().getRoot().findContainersForLocationURI(locationURI));
  }

}
