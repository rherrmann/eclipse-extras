package com.codeaffine.extras.archive.internal.viewer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.DirectoryEntry;

public class ArchiveContentProviderTest {

  private ArchiveContentProvider contentProvider;

  @Before
  public void setUp() {
    contentProvider = new ArchiveContentProvider();
  }

  @Test
  public void testGetParent() throws Exception {
    Object parent = contentProvider.getParent(new Object());

    assertNull(parent);
  }

  @Test
  public void testGetElements() throws Exception {
    Object[] elements = contentProvider.getElements(new Object());

    assertNull(elements);
  }

  @Test
  public void testHasChildrenWithObject() throws Exception {
    boolean hasChildren = contentProvider.hasChildren(new Object());

    assertFalse(hasChildren);
  }

  @Test
  public void testHasChildrenWithParentArchiveEntry() throws Exception {
    DirectoryEntry directoryEntry = mock(DirectoryEntry.class);
    when(directoryEntry.hasChildren()).thenReturn(true);

    boolean hasChildren = contentProvider.hasChildren(directoryEntry);

    assertTrue(hasChildren);
  }

  @Test
  public void testHasChildrenWithIFile() throws Exception {
    IFile file = mock(IFile.class);

    boolean hasChildren = contentProvider.hasChildren(file);

    assertFalse(hasChildren);
  }

  @Test
  public void testHasChildrenWithZipFile() throws Exception {
    IFile file = createZipFile();

    boolean hasChildren = contentProvider.hasChildren(file);

    assertTrue(hasChildren);
  }

  @Test
  public void testGetChildrenWithNonFilesystemZipFile() throws Exception {
    IFile zipFile = createZipFile();

    Object[] children = contentProvider.getChildren(zipFile);

    assertNull(children);
  }

  private static IFile createZipFile() throws CoreException {
    IFile result = mock(IFile.class);
    IContentType contentType = mock(IContentType.class);
    when(contentType.getId()).thenReturn("com.codeaffine.extras.archive.contenttype.ZipContentType");
    IContentDescription contentDescription = mock(IContentDescription.class);
    when(contentDescription.getContentType()).thenReturn(contentType);
    when(result.getContentDescription()).thenReturn(contentDescription);
    return result;
  }
}
