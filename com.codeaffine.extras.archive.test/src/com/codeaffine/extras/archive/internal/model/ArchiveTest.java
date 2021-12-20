package com.codeaffine.extras.archive.internal.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;

public class ArchiveTest {

  @Test
  public void testGetName() throws Exception {
    Archive archive = new Archive("name", null);

    assertEquals("name", archive.getName());
  }

  @Test
  public void testGetLocation() {
    IPath location = mock(IPath.class);

    Archive archive = new Archive(null, location);

    assertEquals(location, archive.getLocation());
  }

  @Test
  public void testGetRootEntry() throws Exception {
    Archive archive = new Archive(null, null);

    DirectoryEntry rootEntry = archive.getRootEntry();

    assertNotNull(rootEntry);
    assertSame(archive, rootEntry.getArchive());
    assertEquals("/", rootEntry.getName());
    assertNull(rootEntry.getParent());
    assertFalse(rootEntry.hasChildren());
    assertEquals(0, rootEntry.getChildren().length);
  }
}
