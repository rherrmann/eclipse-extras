package com.codeaffine.extras.archive.internal.editor;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.content.IContentType;
import com.codeaffine.extras.archive.internal.model.FileEntry;
import com.codeaffine.extras.archive.internal.util.StatusUtil;

public class ArchiveEntryStorage extends PlatformObject implements IStorage {
  private final FileEntry fileEntry;
  private final IContentType contentType;

  public ArchiveEntryStorage(FileEntry fileEntry, IContentType contentType) {
    this.fileEntry = fileEntry;
    this.contentType = contentType;
  }

  @Override
  public InputStream getContents() throws CoreException {
    try {
      return fileEntry.open();
    } catch (IOException ioe) {
      handleException(ioe);
      return null;
    }
  }

  @Override
  public IPath getFullPath() {
    return null;
  }

  @Override
  public String getName() {
    return fileEntry.getName();
  }

  public String getArchiveName() {
    return fileEntry.getArchive().getName();
  }

  public IContentType getContentType() {
    return contentType;
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }

  @Override
  public boolean equals(Object object) {
    boolean result = false;
    if (object instanceof ArchiveEntryStorage) {
      ArchiveEntryStorage archiveEntryStorage = (ArchiveEntryStorage) object;
      result = archiveEntryStorage.fileEntry.equals(fileEntry);
    }
    return result;
  }

  @Override
  public int hashCode() {
    return fileEntry.hashCode();
  }

  @Override
  public String toString() {
    return "ArchiveEntry[" + getArchiveName() + "::" + fileEntry.getName() + "]";
  }

  private static void handleException(Exception exception) throws CoreException {
    IStatus status = StatusUtil.createErrorStatus(exception);
    throw new CoreException(status);
  }
}
