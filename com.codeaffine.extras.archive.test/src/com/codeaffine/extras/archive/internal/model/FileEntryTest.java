package com.codeaffine.extras.archive.internal.model;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileEntryTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private Archive archive;
  private File archiveLocation;

  @Before
  public void setUp() throws IOException {
    archiveLocation = new File(tempFolder.getRoot(), "test.zip");
    archive = new Archive("archive-name", new Path(archiveLocation.getCanonicalPath()));
  }

  @Test
  public void testOpen() throws Exception {
    provisionFile("file.zip");
    ZipEntry zipEntry = getZipFileEntry();
    DirectoryEntry directoryEntry = new DirectoryEntry(archive, "");
    FileEntry archiveEntry = new FileEntry(directoryEntry, zipEntry);

    InputStream inputStream = archiveEntry.open();
    inputStream.close();

    assertNotNull(inputStream);
  }

  @Test
  public void testGetArchive() throws Exception {
    DirectoryEntry directoryEntry = new DirectoryEntry(archive, "");
    FileEntry fileEntry = new FileEntry(directoryEntry, mock(ZipEntry.class));

    Archive returnedArchive = fileEntry.getArchive();

    assertSame(archive, returnedArchive);
  }

  private void provisionFile(String name) throws IOException {
    try (InputStream inputStream = getClass().getResourceAsStream(name)) {
      Files.copy(inputStream, archiveLocation.toPath(), REPLACE_EXISTING);
    }
  }

  private ZipEntry getZipFileEntry() throws IOException {
    try (ZipFile zipFile = new ZipFile(archiveLocation)) {
      return zipFile.entries().nextElement(); // file.txt
    }
  }
}
