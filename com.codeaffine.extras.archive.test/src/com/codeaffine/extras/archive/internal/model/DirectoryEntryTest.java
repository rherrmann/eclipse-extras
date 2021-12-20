package com.codeaffine.extras.archive.internal.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.util.zip.ZipEntry;
import org.junit.Before;
import org.junit.Test;

public class DirectoryEntryTest {

  private Archive archive;
  private DirectoryEntry directoryEntry;

  @Test
  public void testConstructorWithParentArchiveEntry() throws Exception {
    DirectoryEntry subDirectoryEntry = new DirectoryEntry(directoryEntry, "sub");

    ArchiveEntry[] children = directoryEntry.getChildren();

    assertEquals(1, children.length);
    assertSame(subDirectoryEntry, children[0]);
  }

  @Test
  public void testHasChildrenWithoutChildren() {
    boolean hasChildren = directoryEntry.hasChildren();

    assertFalse(hasChildren);
  }

  @Test
  public void testGetChildrenWithoutChildren() throws Exception {
    ArchiveEntry[] children = directoryEntry.getChildren();

    assertEquals(0, children.length);
  }

  @SuppressWarnings("unused")
  @Test
  public void testHasChildrenWithChildren() throws Exception {
    new FileEntry(directoryEntry, mock(ZipEntry.class));

    boolean hasChildren = directoryEntry.hasChildren();

    assertTrue(hasChildren);
  }

  @Test
  public void testGetChildrenWithChildren() throws Exception {
    FileEntry fileEntry = new FileEntry(directoryEntry, mock(ZipEntry.class));

    ArchiveEntry[] children = directoryEntry.getChildren();

    assertEquals(1, children.length);
    assertSame(fileEntry, children[0]);
  }

  @Test
  public void testGetArchive() throws Exception {
    DirectoryEntry subDirectoryEntry = new DirectoryEntry(directoryEntry, "");

    Archive returnedArchive = subDirectoryEntry.getArchive();

    assertSame(archive, returnedArchive);
  }

  @Before
  public void setUp() {
    archive = mock(Archive.class);
    directoryEntry = new DirectoryEntry(archive, "");
  }
}
