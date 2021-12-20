package com.codeaffine.extras.archive.internal.viewer;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.codeaffine.extras.archive.internal.util.StatusUtil;

public class ArchiveContentProviderWrapper implements ITreeContentProvider {
  private ITreeContentProvider delegate;

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    updateDelegate();
    logWarning();
  }

  @Override
  public Object[] getElements(Object inputElement) {
    return delegate.getElements(inputElement);
  }

  @Override
  public Object[] getChildren(Object parentElement) {
    return delegate.getChildren(parentElement);
  }

  @Override
  public Object getParent(Object element) {
    return delegate.getParent(element);
  }

  @Override
  public boolean hasChildren(Object element) {
    return delegate.hasChildren(element);
  }

  @Override
  public void dispose() {
    delegate.dispose();
  }

  private void updateDelegate() {
    String fileSystemScheme = ResourcesPlugin.getWorkspace().getRoot().getLocationURI().getScheme();
    if ("file".equals(fileSystemScheme)) {
      delegate = new ArchiveContentProvider();
    } else {
      delegate = new NullContentProvider();
    }
  }

  private void logWarning() {
    if (delegate instanceof NullContentProvider) {
      String msg = "The Zip Archive extension for the Project Explorer can only operate on "
          + "workspaces backed by the local filesystem.";
      StatusUtil.log(IStatus.WARNING, msg);
    }
  }

  private static class NullContentProvider implements ITreeContentProvider {
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    @Override
    public Object[] getElements(Object inputElement) {
      return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
      return null;
    }

    @Override
    public Object getParent(Object element) {
      return null;
    }

    @Override
    public boolean hasChildren(Object element) {
      return false;
    }

    @Override
    public void dispose() {}
  }
}
