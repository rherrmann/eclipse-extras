package com.codeaffine.extras.archive.internal.model;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class ArchiveReader {

  public static ArchiveEntry[] read(IFile file) throws IOException {
    String archiveName = file.getFullPath().toString();
    IPath location = file.getLocation();
    Archive archive = new ArchiveReader(archiveName, location).read();
    return archive.getRootEntry().getChildren();
  }

  public static ArchiveEntry[] read(String archiveName, File file) throws IOException {
    IPath location = new Path(file.getCanonicalPath());
    Archive archive = new ArchiveReader(archiveName, location).read();
    return archive.getRootEntry().getChildren();
  }

  private final Archive archive;
  private ZipFile zipFile;

  ArchiveReader(String archiveName, IPath fileLocation) {
    archive = new Archive(archiveName, fileLocation);
  }

  Archive read() throws IOException {
    openZipFile();
    try {
      readEntries();
      return archive;
    } finally {
      closeZipFile();
    }
  }

  private void openZipFile() throws ZipException, IOException {
    zipFile = new ZipFile(archive.getLocation().toFile().getCanonicalFile());
  }

  private void closeZipFile() throws IOException {
    zipFile.close();
  }

  private void readEntries() {
    Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
    while (zipEntries.hasMoreElements()) {
      ZipEntry zipEntry = zipEntries.nextElement();
      createArchiveEntry(zipEntry);
    }
  }

  @SuppressWarnings("unused")
  private void createArchiveEntry(ZipEntry zipEntry) {
    IPath path = new Path(zipEntry.getName());
    if (zipEntry.isDirectory()) {
      makeDirectories(path);
    } else {
      path = path.removeLastSegments(1);
      DirectoryEntry parent = makeDirectories(path);
      new FileEntry(parent, zipEntry);
    }
  }

  private DirectoryEntry makeDirectories(IPath path) {
    DirectoryEntry result = archive.getRootEntry();
    String[] segments = path.segments();
    for (int i = 0; i < segments.length; i++) {
      String segment = segments[i];
      result = makeDirectory(result, segment);
    }
    return result;
  }

  private static DirectoryEntry makeDirectory(DirectoryEntry parent, String name) {
    DirectoryEntry result = null;
    ArchiveEntry[] children = parent.getChildren();
    for (int i = 0; result == null && i < children.length; i++) {
      if (children[i] instanceof DirectoryEntry && name.equals(children[i].getName())) {
        result = (DirectoryEntry) children[i];
      }
    }
    if (result == null) {
      result = new DirectoryEntry(parent, name);
    }
    return result;
  }
}
