package com.codeaffine.extras.archive.internal.extract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.codeaffine.extras.archive.internal.model.ArchiveEntry;
import com.codeaffine.extras.archive.internal.model.FileEntry;
import com.codeaffine.extras.test.util.ProjectHelper;

public class ArchiveExtractorJobTest {
  private static final String ARCHIVE_ENTRY_NAME = "document.txt";

  @Rule
  public ProjectHelper projectHelper = new ProjectHelper();
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private IProject project;
  private File directory;
  private IProgressMonitor progressMonitor;

  @Before
  public void setUp() throws Exception {
    project = projectHelper.getProject();
    directory = tempFolder.getRoot();
    progressMonitor = mock(IProgressMonitor.class);
  }

  @Test
  public void testCancelWhileExtractingToFilesystem() throws Exception {
    cancelProgressMonitor();
    ExtractLocation extractLocation = new ExtractLocation(directory.getCanonicalPath());
    ArchiveEntry archiveEntry = createFileEntry(ARCHIVE_ENTRY_NAME);
    TestArchiveExtractorJob job = createArchiveExtractorJob(archiveEntry, extractLocation);

    IStatus status = job.run(progressMonitor);

    assertTrue(status.isOK());
    assertFalse(isExtractedToFilesystem(archiveEntry));
  }

  @Test
  public void testCancelWhileExtractingToWorkspace() throws Exception {
    cancelProgressMonitor();
    ExtractLocation extractLocation = new ExtractLocation(project.getName());
    ArchiveEntry archiveEntry = createFileEntry(ARCHIVE_ENTRY_NAME);
    TestArchiveExtractorJob job = createArchiveExtractorJob(archiveEntry, extractLocation);

    IStatus status = job.run(progressMonitor);

    assertTrue(status.isOK());
    assertFalse(isExtractedToWorkspace(archiveEntry));
  }

  @Test
  public void testExceptionWhileExtractingToFilesystem() throws Exception {
    ExtractLocation extractLocation = new ExtractLocation(project.getName());
    ArchiveEntry archiveEntry = createCorruptFileEntry(ARCHIVE_ENTRY_NAME);
    TestArchiveExtractorJob job = createArchiveExtractorJob(archiveEntry, extractLocation);

    IStatus status = job.run(progressMonitor);

    assertEquals(IStatus.ERROR, status.getSeverity());
    assertEquals(CoreException.class, status.getException().getClass());
    assertFalse(isExtractedToWorkspace(archiveEntry));
  }

  @Test
  public void testExceptionWhileExtractingToWorkspace() throws Exception {
    ExtractLocation extractLocation = new ExtractLocation(directory.getCanonicalPath());
    ArchiveEntry archiveEntry = createCorruptFileEntry(ARCHIVE_ENTRY_NAME);
    TestArchiveExtractorJob job = createArchiveExtractorJob(archiveEntry, extractLocation);

    IStatus status = job.run(progressMonitor);

    assertEquals(IStatus.ERROR, status.getSeverity());
    assertEquals(CoreException.class, status.getException().getClass());
    assertFalse(isExtractedToFilesystem(archiveEntry));
  }

  @Test
  public void testExtractToFilesystem() throws Exception {
    ExtractLocation extractLocation = new ExtractLocation(directory.getCanonicalPath());
    ArchiveEntry archiveEntry = createFileEntry(ARCHIVE_ENTRY_NAME);
    TestArchiveExtractorJob job = createArchiveExtractorJob(archiveEntry, extractLocation);

    IStatus status = job.run(progressMonitor);

    assertTrue(status.isOK());
    assertTrue(isExtractedToFilesystem(archiveEntry));
  }

  @Test
  public void testExtractToWorkspace() throws Exception {
    ExtractLocation extractLocation = new ExtractLocation(project.getName());
    ArchiveEntry archiveEntry = createFileEntry(ARCHIVE_ENTRY_NAME);
    TestArchiveExtractorJob job = createArchiveExtractorJob(archiveEntry, extractLocation);

    IStatus status = job.run(progressMonitor);

    assertTrue(status.isOK());
    assertTrue(isExtractedToWorkspace(archiveEntry));
  }

  private void cancelProgressMonitor() {
    when(progressMonitor.isCanceled()).thenReturn(true);
  }

  private static TestArchiveExtractorJob createArchiveExtractorJob(ArchiveEntry archiveEntry,
      ExtractLocation extractLocation) {
    ArchiveEntry[] archiveEntries = {archiveEntry};
    return new TestArchiveExtractorJob(archiveEntries, extractLocation);
  }

  private static FileEntry createFileEntry(String name) throws IOException {
    FileEntry result = mock(FileEntry.class);
    when(result.getName()).thenReturn(name);
    when(result.open()).thenReturn(new ByteArrayInputStream(new byte[0]));
    return result;
  }

  private static ArchiveEntry createCorruptFileEntry(String name) throws IOException {
    FileEntry result = mock(FileEntry.class);
    when(result.getName()).thenReturn(name);
    when(result.open()).thenThrow(new IOException());
    return result;
  }

  private boolean isExtractedToFilesystem(ArchiveEntry archiveEntry) {
    return new File(directory, archiveEntry.getName()).exists();
  }

  private boolean isExtractedToWorkspace(ArchiveEntry archiveEntry) {
    return project.getFile(archiveEntry.getName()).exists();
  }

  private static class TestArchiveExtractorJob extends ArchiveExtractorJob {
    TestArchiveExtractorJob(ArchiveEntry[] archiveEntries, ExtractLocation extractLocation) {
      super(archiveEntries, extractLocation);
    }

    @Override
    public IStatus run(IProgressMonitor progressMonitor) {
      return super.run(progressMonitor);
    }
  }
}
