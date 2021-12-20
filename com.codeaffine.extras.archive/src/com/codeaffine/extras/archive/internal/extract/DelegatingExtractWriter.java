package com.codeaffine.extras.archive.internal.extract;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

class DelegatingExtractWriter implements ExtractWriter {
  private final ExtractWriter delegate;

  DelegatingExtractWriter(ExtractLocation location) {
    if (location.isWorkspaceRelative()) {
      delegate = new WorkspaceWriter(location.getPath());
    } else {
      delegate = new FilesystemWriter(location.getPath().toFile());
    }
  }

  @Override
  public void write(IPath path, InputStream inputStream) throws CoreException {
    delegate.write(path, inputStream);
  }
}
