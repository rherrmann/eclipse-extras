package com.codeaffine.extras.archive.internal.extract;

import static org.eclipse.core.resources.IContainer.INCLUDE_HIDDEN;
import static org.eclipse.core.resources.IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS;
import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import com.codeaffine.extras.archive.internal.util.StatusUtil;

class ExtractLocation {
  private final IPath path;

  ExtractLocation(String location) {
    this.path = new Path(location);
  }

  IStatus validate() {
    IStatus result = Status.OK_STATUS;
    if (result.isOK()) {
      result = checkPathNotEmpty();
    }
    if (result.isOK()) {
      result = checkPathInWorkspace();
    }
    if (result.isOK()) {
      result = checkPathInFilesystem();
    }
    if (result.isOK()) {
      result = checkWorkspacePathIsEmpty();
    }
    if (result.isOK()) {
      result = checkFilesystemPathIsEmpty();
    }
    return result;
  }

  boolean isWorkspaceRelative() {
    return ResourcesPlugin.getWorkspace().getRoot().findMember(path) != null;
  }

  IPath getPath() {
    return path;
  }

  private IStatus checkPathNotEmpty() {
    IStatus result = Status.OK_STATUS;
    if (path.isEmpty()) {
      result = createErrorStatus("Location must be specified");
    }
    return result;
  }

  private IStatus checkPathInWorkspace() {
    IStatus result = Status.OK_STATUS;
    if (isWorkspaceRelative() && !isContainer()) {
      result = createErrorStatus("Folder does not exist in workspace");
    }
    return result;
  }

  private IStatus checkPathInFilesystem() {
    IStatus result = Status.OK_STATUS;
    if (!isWorkspaceRelative() && !isDirectory()) {
      result = createErrorStatus("External folder does not exist");
    }
    return result;
  }

  private IStatus checkWorkspacePathIsEmpty() {
    IStatus result = Status.OK_STATUS;
    if (isWorkspaceRelative()) {
      if (!isContainerEmpty()) {
        result = createWarningStatus("Folder is not empty, its content may be overwritten");
      }
    }
    return result;
  }

  private IStatus checkFilesystemPathIsEmpty() {
    IStatus result = Status.OK_STATUS;
    if (!isWorkspaceRelative()) {
      if (new File(path.toOSString()).list().length > 0) {
        result = createWarningStatus("Folder is not empty, its content may be overwritten");
      }
    }
    return result;
  }

  private boolean isContainerEmpty() {
    boolean result = true;
    IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
    if (resource instanceof IContainer) {
      IContainer container = (IContainer) resource;
      try {
        IResource[] members = container.members(INCLUDE_HIDDEN | INCLUDE_TEAM_PRIVATE_MEMBERS);
        result = members.length == 0;
      } catch (CoreException ignore) {
      }
    }
    return result;
  }

  private boolean isDirectory() {
    return path.toFile().isDirectory();
  }

  private boolean isContainer() {
    return ResourcesPlugin.getWorkspace().getRoot().findMember(path) instanceof IContainer;
  }

  private static IStatus createErrorStatus(String message) {
    return StatusUtil.createStatus(IStatus.ERROR, message, null);
  }

  private static IStatus createWarningStatus(String message) {
    return StatusUtil.createStatus(IStatus.WARNING, message, null);
  }
}
