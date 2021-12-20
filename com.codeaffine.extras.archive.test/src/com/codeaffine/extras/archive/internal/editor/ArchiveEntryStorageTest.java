package com.codeaffine.extras.archive.internal.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.Archive;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class ArchiveEntryStorageTest {

  private FileEntry fileEntry;
  private ArchiveEntryStorage storage;

  @Test
  public void testGetContents() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    when(fileEntry.open()).thenReturn(inputStream);

    InputStream contents = storage.getContents();

    verify(fileEntry).open();
    assertSame(inputStream, contents);
  }

  @Test
  public void testGetContentsWithIOException() throws Exception {
    IOException ioException = new IOException();
    when(fileEntry.open()).thenThrow(ioException);

    try {
      storage.getContents();
      fail();
    } catch (CoreException expected) {
      assertSame(ioException, expected.getCause());
    }
  }

  @Test
  public void testGetFullPath() throws Exception {
    assertNull(storage.getFullPath());
  }

  @Test
  public void testIsReadOnly() throws Exception {
    assertTrue(storage.isReadOnly());
  }

  @Test
  public void testGetName() throws Exception {
    when(fileEntry.getName()).thenReturn("name");

    assertEquals(fileEntry.getName(), storage.getName());
  }

  @Test
  public void testGetArchiveName() throws Exception {
    Archive archive = new Archive("archive-name", null);
    when(fileEntry.getArchive()).thenReturn(archive);

    assertEquals(fileEntry.getArchive().getName(), storage.getArchiveName());
  }

  @Before
  public void setUp() {
    IContentType contentType = mock(IContentType.class);
    fileEntry = mock(FileEntry.class);
    storage = new ArchiveEntryStorage(fileEntry, contentType);
  }
}
