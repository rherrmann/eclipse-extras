package com.codeaffine.extras.archive.internal.model;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class FileEntry extends ArchiveEntry {
  private final ZipEntry zipEntry;

  public FileEntry(DirectoryEntry parent, ZipEntry zipEntry) {
    super(parent);
    this.zipEntry = zipEntry;
  }

  @Override
  public String getName() {
    return new Path(zipEntry.getName()).lastSegment();
  }

  public InputStream open() throws IOException {
    IPath location = getArchive().getLocation();
    final ZipFile zipFile = new ZipFile(location.toFile());
    return new ZipEntryInputStream(zipFile, zipEntry);
  }

  @Override
  public Archive getArchive() {
    return parent.getArchive();
  }

  @Override
  protected void addChild(ArchiveEntry archiveEntry) {
    throw new UnsupportedOperationException();
  }

  private static class ZipEntryInputStream extends FilterInputStream {
    private final ZipFile zipFile;

    private ZipEntryInputStream(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
      super(zipFile.getInputStream(zipEntry));
      this.zipFile = zipFile;
    }

    @Override
    public void close() throws IOException {
      zipFile.close();
      super.close();
    }
  }
}
