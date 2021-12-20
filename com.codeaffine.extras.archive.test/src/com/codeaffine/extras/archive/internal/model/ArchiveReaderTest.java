package com.codeaffine.extras.archive.internal.model;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ArchiveReaderTest {
  private static final String ARCHIVE_NAME = "archive-name";

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private File file;

  @Before
  public void setUp() {
    file = new File(tempFolder.getRoot(), "test.zip");
  }

  @Test
  public void testRead() throws Exception {
    provisionZipFile("file.zip");

    ArchiveReader archiveReader = createArchiveReader();
    Archive archive = archiveReader.read();

    assertEquals(ARCHIVE_NAME, archive.getName());
    assertEquals(file.getCanonicalFile(), archive.getLocation().toFile().getCanonicalFile());
  }

  @Test
  public void testRead_file_zip() throws Exception {
    provisionZipFile("file.zip");
    ArchiveReader archiveReader = createArchiveReader();

    Archive archive = archiveReader.read();
    ArchiveEntry[] archiveEntries = archive.getRootEntry().getChildren();

    assertEquals(1, archiveEntries.length);
    ArchiveEntry archiveEntry = archiveEntries[0];
    assertTrue(archiveEntry instanceof FileEntry);
    assertEquals("file.txt", archiveEntry.getName());
  }

  @Test
  public void testRead_file_in_folder_zip() throws Exception {
    provisionZipFile("file_in_folder.zip");
    ArchiveReader archiveReader = createArchiveReader();

    Archive archive = archiveReader.read();
    ArchiveEntry[] archiveEntries = archive.getRootEntry().getChildren();

    assertEquals(1, archiveEntries.length);
    ArchiveEntry archiveEntry = archiveEntries[0];
    assertTrue(archiveEntry instanceof DirectoryEntry);
    assertEquals("folder", archiveEntry.getName());
    DirectoryEntry folder = (DirectoryEntry) archiveEntry;
    assertEquals(1, folder.getChildren().length);
    assertTrue(folder.getChildren()[0] instanceof FileEntry);
    ArchiveEntry file = folder.getChildren()[0];
    assertEquals("file.txt", file.getName());
  }

  private void provisionZipFile(String resourceName) throws IOException {
    try (InputStream inputStream = getClass().getResourceAsStream(resourceName)) {
      Files.copy(inputStream, file.toPath(), REPLACE_EXISTING);
    }
  }

  private ArchiveReader createArchiveReader() {
    return new ArchiveReader(ARCHIVE_NAME, new Path(file.getAbsolutePath()));
  }
}
