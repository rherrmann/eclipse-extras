package com.codeaffine.extras.archive.internal.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;

public class ArchiveEntryTest {

  private Archive archive;

  @Before
  public void setUp() {
    archive = mock(Archive.class);
  }

  @Test
  public void testGetEntryName() {
    String entryName = "entryName";
    ArchiveEntry archiveEntry = new TestArchiveEntry(null, entryName);

    String name = archiveEntry.getName();

    assertEquals(entryName, name);
  }

  @Test
  public void testGetParent() throws Exception {
    ArchiveEntry parent = mock(ArchiveEntry.class);
    ArchiveEntry archiveEntry = new TestArchiveEntry(parent);

    ArchiveEntry returnedParent = archiveEntry.getParent();

    assertSame(parent, returnedParent);
  }

  @Test
  public void testEquals() throws Exception {
    String name = "1";
    ArchiveEntry entry1 = new TestArchiveEntry(archive, name);
    ArchiveEntry entry2 = new TestArchiveEntry(archive, name);

    boolean equals = entry1.equals(entry2);

    assertTrue(equals);
  }

  @Test
  public void testHashCode() throws Exception {
    String name = "1";
    ArchiveEntry entry1 = new TestArchiveEntry(archive, name);
    ArchiveEntry entry2 = new TestArchiveEntry(archive, name);

    int hashCode1 = entry1.hashCode();
    int hashCode2 = entry2.hashCode();

    assertEquals(hashCode1, hashCode2);
  }

  @Test
  public void testEqualsWithOtherClass() throws Exception {
    ArchiveEntry entry1 = new TestArchiveEntry(archive, "foo");

    boolean equals = entry1.equals(new Object());

    assertFalse(equals);
  }

  @Test
  public void testEqualsWithOtherZipEntry() throws Exception {
    ArchiveEntry entry1 = new TestArchiveEntry(archive, "one");
    ArchiveEntry entry2 = new TestArchiveEntry(archive, "another");

    boolean equals = entry1.equals(entry2);

    assertFalse(equals);
  }

  @Test
  public void testEqualsWithOtherFile() throws Exception {
    ArchiveEntry entry1 = new TestArchiveEntry(archive, "entry");
    ArchiveEntry entry2 = new TestArchiveEntry(mock(Archive.class), "entry");

    boolean equals = entry1.equals(entry2);

    assertFalse(equals);
  }

  private static class TestArchiveEntry extends ArchiveEntry {
    private final String name;
    private final Archive archive;

    TestArchiveEntry(ArchiveEntry parent) {
      super(parent);
      this.archive = null;
      this.name = null;
    }

    TestArchiveEntry(Archive archive, String name) {
      super(null);
      this.archive = archive;
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public Archive getArchive() {
      return archive;
    }

    @Override
    protected void addChild(ArchiveEntry archiveEntry) {}
  }

}
