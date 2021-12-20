package com.codeaffine.extras.archive.internal.viewer;

import java.io.IOException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.codeaffine.extras.archive.internal.contenttype.ContentTypes;
import com.codeaffine.extras.archive.internal.model.ArchiveReader;
import com.codeaffine.extras.archive.internal.model.DirectoryEntry;
import com.codeaffine.extras.archive.internal.util.StatusUtil;

class ArchiveContentProvider implements ITreeContentProvider {

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

  @Override
  public Object[] getElements(Object inputElement) {
    return null;
  }

  @Override
  public Object getParent(Object element) {
    return null;
  }

  @Override
  public Object[] getChildren(Object parentElement) {
    Object[] result = null;
    if (parentElement instanceof IFile) {
      result = getChildren((IFile) parentElement);
    } else if (parentElement instanceof DirectoryEntry) {
      result = getChildren((DirectoryEntry) parentElement);
    }
    return result;
  }

  @Override
  public boolean hasChildren(Object element) {
    return isArchiveFile(element) || isDirectoryEntry(element);
  }

  @Override
  public void dispose() {}

  private static boolean isArchiveFile(Object element) {
    boolean result = false;
    if (element instanceof IFile) {
      IFile file = (IFile) element;
      IContentDescription contentDescription = ContentTypes.getContentDescription(file);
      result = ContentTypes.isZipContentType(contentDescription.getContentType());
    }
    return result;
  }

  private static boolean isDirectoryEntry(Object element) {
    boolean result = false;
    if (element instanceof DirectoryEntry) {
      DirectoryEntry directoryEntry = (DirectoryEntry) element;
      result = directoryEntry.hasChildren();
    }
    return result;
  }

  private static Object[] getChildren(IFile file) {
    Object[] result = null;
    IPath location = file.getLocation();
    if (location != null) {
      try {
        result = ArchiveReader.read(file);
      } catch (IOException ioe) {
        StatusUtil.logError(ioe);
      }
    }
    return result;
  }

  private static Object[] getChildren(DirectoryEntry directoryEntry) {
    return directoryEntry.getChildren();
  }
}
