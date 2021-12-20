package com.codeaffine.extras.archive.internal.extract;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import com.codeaffine.extras.archive.internal.util.StatusUtil;

class FilesystemWriter implements ExtractWriter {
  private final File rootDirectory;

  FilesystemWriter(File rootDirectory) {
    this.rootDirectory = rootDirectory;
  }

  @Override
  public void write(IPath path, InputStream inputStream) throws CoreException {
    File destinationDirectory = makeDirectories(path);
    File destinationFile = new File(destinationDirectory, path.lastSegment());
    try {
      write(inputStream, destinationFile);
    } catch (IOException ioe) {
      throw new CoreException(StatusUtil.createErrorStatus(ioe));
    }
  }

  private static void write(InputStream inputStream, File destinationFile) throws IOException {
    try (OutputStream outputStream = createOutputStream(destinationFile)) {
      write(inputStream, outputStream);
    }
  }

  private static void write(InputStream inputStream, OutputStream outputStream) throws IOException {
    int read = inputStream.read();
    while (read != -1) {
      outputStream.write(read);
      read = inputStream.read();
    }
  }

  private File makeDirectories(IPath path) {
    IPath directories = path.removeLastSegments(1);
    File result = new File(rootDirectory, directories.toPortableString());
    result.mkdirs();
    return result;
  }

  private static OutputStream createOutputStream(File file) throws IOException {
    return new BufferedOutputStream(new FileOutputStream(file));
  }
}
