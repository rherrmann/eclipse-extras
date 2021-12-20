package com.codeaffine.extras.archive.internal.extract;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.ArchiveEntry;
import com.codeaffine.extras.archive.internal.model.DirectoryEntry;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class ArchiveExtractorTest {

  private ExtractWriter extractWriter;
  private IProgressMonitor progressMonitor;

  @Test
  public void testExtractWithCanceledProgressMonitor() throws Exception {
    when(progressMonitor.isCanceled()).thenReturn(true);
    ArchiveExtractor extractor = new ArchiveExtractor(mock(FileEntry.class), progressMonitor);

    try {
      extractor.extract(extractWriter);
      fail();
    } catch (OperationCanceledException expected) {
    }
  }

  @Test
  public void testExtractFile() throws Exception {
    String name = "file.txt";
    InputStream inputStream = mock(InputStream.class);
    FileEntry fileEntry = createFileEntry(name, inputStream);

    ArchiveExtractor extractor = new ArchiveExtractor(fileEntry, progressMonitor);
    extractor.extract(extractWriter);

    verify(extractWriter).write(eq(new Path(name)), any(InputStream.class));
  }

  @Test
  public void testExtractRootDirectory() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    FileEntry fileEntry = createFileEntry("file.txt", inputStream);
    DirectoryEntry directoryEntry = createDirectoryEntry("path", fileEntry);

    ArchiveExtractor extractor = new ArchiveExtractor(directoryEntry, progressMonitor);
    extractor.extract(extractWriter);

    verify(extractWriter).write(eq(new Path("path/file.txt")), any(InputStream.class));
  }

  @Test
  public void testExtractChildDirectory() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    FileEntry fileEntry = createFileEntry("file.txt", inputStream);
    DirectoryEntry subDirectoryEntry = createDirectoryEntry("to", fileEntry);
    createDirectoryEntry("path", subDirectoryEntry);

    ArchiveExtractor extractor = new ArchiveExtractor(subDirectoryEntry, progressMonitor);
    extractor.extract(extractWriter);

    verify(extractWriter).write(eq(new Path("to/file.txt")), any(InputStream.class));
  }

  @Test
  public void testExtractNestedDirectory() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    FileEntry fileEntry = createFileEntry("file.txt", inputStream);
    DirectoryEntry subDirectoryEntry = createDirectoryEntry("to", fileEntry);
    DirectoryEntry directoryEntry = createDirectoryEntry("path", subDirectoryEntry);

    ArchiveExtractor extractor = new ArchiveExtractor(directoryEntry, progressMonitor);
    extractor.extract(extractWriter);

    verify(extractWriter).write(eq(new Path("path/to/file.txt")), any(InputStream.class));
  }

  @Before
  public void setUp() {
    extractWriter = mock(ExtractWriter.class);
    progressMonitor = mock(IProgressMonitor.class);
  }

  private static FileEntry createFileEntry(String name, InputStream inputStream) throws IOException {
    FileEntry result = mock(FileEntry.class);
    when(result.getName()).thenReturn(name);
    when(result.open()).thenReturn(inputStream);
    return result;
  }

  private static DirectoryEntry createDirectoryEntry(String name, ArchiveEntry... children) {
    DirectoryEntry result = mock(DirectoryEntry.class);
    when(result.getName()).thenReturn(name);
    when(result.getChildren()).thenReturn(children);
    for (ArchiveEntry child : children) {
      when(child.getParent()).thenReturn(result);
    }
    return result;
  }
}
