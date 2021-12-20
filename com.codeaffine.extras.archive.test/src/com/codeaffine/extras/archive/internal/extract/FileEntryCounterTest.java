package com.codeaffine.extras.archive.internal.extract;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.ArchiveEntry;
import com.codeaffine.extras.archive.internal.model.DirectoryEntry;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class FileEntryCounterTest {

  @Test
  public void testCountWithFiles() {
    ArchiveEntry[] archiveEntries = new ArchiveEntry[] {createFileEntry(), createFileEntry()};
    FileEntryCounter counter = new FileEntryCounter(archiveEntries);

    int count = counter.count();

    assertEquals(2, count);
  }

  @Test
  public void testCountWithEmptyDirectory() {
    ArchiveEntry[] archiveEntries = new ArchiveEntry[] {createDirectoryEntry()};
    FileEntryCounter counter = new FileEntryCounter(archiveEntries);

    int count = counter.count();

    assertEquals(0, count);
  }

  @Test
  public void testCountWithFilesInDirectory() {
    ArchiveEntry[] archiveEntries = new ArchiveEntry[] {createDirectoryEntry(createFileEntry(), createFileEntry())};
    FileEntryCounter counter = new FileEntryCounter(archiveEntries);

    int count = counter.count();

    assertEquals(2, count);
  }

  @Test
  public void testCountWithFilesInSubDirectory() {
    ArchiveEntry[] archiveEntries =
        new ArchiveEntry[] {createDirectoryEntry(createDirectoryEntry(createFileEntry(), createFileEntry()))};
    FileEntryCounter counter = new FileEntryCounter(archiveEntries);

    int count = counter.count();

    assertEquals(2, count);
  }

  private static FileEntry createFileEntry() {
    return mock(FileEntry.class);
  }

  private static DirectoryEntry createDirectoryEntry(ArchiveEntry... children) {
    DirectoryEntry result = mock(DirectoryEntry.class);
    when(result.getChildren()).thenReturn(children);
    for (ArchiveEntry child : children) {
      when(child.getParent()).thenReturn(result);
    }
    return result;
  }
}
