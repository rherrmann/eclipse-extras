package com.codeaffine.extras.archive.internal.extract;

import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

public class WorkspaceWriter implements ExtractWriter {
  private final IContainer rootFolder;
  private final IProgressMonitor progressMonitor;

  public WorkspaceWriter(IPath path) {
    rootFolder = (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(path);
    progressMonitor = new NullProgressMonitor();
  }

  @Override
  public void write(IPath path, InputStream inputStream) throws CoreException {
    IContainer destinationFolder = makeFolders(path.removeLastSegments(1));
    IFile destinationFile = destinationFolder.getFile(new Path(path.lastSegment()));
    if (destinationFile.exists()) {
      destinationFile.delete(IResource.FORCE | IResource.KEEP_HISTORY, progressMonitor);
    }
    destinationFile.create(inputStream, IResource.FORCE | IResource.REPLACE, progressMonitor);
  }

  private IContainer makeFolders(IPath path) throws CoreException {
    IContainer currentFolder = rootFolder;
    for (String segment : path.segments()) {
      currentFolder = makeFolder(currentFolder, segment);
    }
    return currentFolder;
  }

  private IFolder makeFolder(IContainer parent, String name) throws CoreException {
    IFolder result = parent.getFolder(new Path(name));
    if (!result.exists()) {
      result.create(true, true, progressMonitor);
    }
    return result;
  }
}
