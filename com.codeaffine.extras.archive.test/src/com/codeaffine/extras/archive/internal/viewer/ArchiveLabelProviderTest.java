package com.codeaffine.extras.archive.internal.viewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.util.zip.ZipEntry;
import org.eclipse.swt.graphics.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.ArchiveEntry;
import com.codeaffine.extras.archive.internal.model.DirectoryEntry;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class ArchiveLabelProviderTest {

  private ArchiveLabelProvider labelProvider;

  @Before
  public void setUp() {
    labelProvider = new ArchiveLabelProvider();
  }

  @After
  public void tearDown() {
    labelProvider.dispose();
  }

  @Test
  public void testGetImageWithArbitraryObject() {
    Image image = labelProvider.getImage(new Object());

    assertNull(image);
  }

  @Test
  public void testGetImageWithFileEntryAndEntryDirectory() throws Exception {
    ArchiveEntry fileEntry = mock(FileEntry.class);
    ArchiveEntry directoryEntry = mock(DirectoryEntry.class);

    Image fileImage = labelProvider.getImage(fileEntry);
    Image directoryImage = labelProvider.getImage(directoryEntry);

    assertNotNull(fileImage);
    assertNotNull(directoryImage);
    assertNotSame(fileImage, directoryImage);
  }

  @Test
  public void testGetText() throws Exception {
    String text = "text";
    DirectoryEntry directoryEntry = new DirectoryEntry(mock(DirectoryEntry.class), text);

    String returnedText = labelProvider.getText(directoryEntry);

    assertEquals(text, returnedText);
  }

  @Test
  public void testDispose() throws Exception {
    DirectoryEntry parent = mock(DirectoryEntry.class);
    ArchiveEntry fileEntry = new FileEntry(parent, mock(ZipEntry.class));
    Image image = labelProvider.getImage(fileEntry);

    labelProvider.dispose();

    assertTrue(image.isDisposed());
  }

}
