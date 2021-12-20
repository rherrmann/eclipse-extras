package com.codeaffine.extras.archive.internal.contenttype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.contenttype.ContentTypes.NullContentDescription;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class ContentTypesTest {

  @Test
  public void testIsZipContentTypeWithNullArgument() {
    boolean zipContentType = ContentTypes.isZipContentType(null);

    assertFalse(zipContentType);
  }

  @Test
  public void testIsZipContentTypeWithInvalidContentType() throws Exception {
    IContentType contentType = createContentType(null);

    boolean zipContentType = ContentTypes.isZipContentType(contentType);

    assertFalse(zipContentType);
  }

  @Test
  public void testIsZipContentTypeWithZipContentType() throws Exception {
    String id = "com.codeaffine.extras.archive.contenttype.ZipContentType";
    IContentType contentType = createContentType(id);

    boolean zipContentType = ContentTypes.isZipContentType(contentType);

    assertTrue(zipContentType);
  }

  @Test
  public void testIsZipContentTypeWithFooContentType() throws Exception {
    IContentType contentType = createContentType("foo");

    boolean zipContentType = ContentTypes.isZipContentType(contentType);

    assertFalse(zipContentType);
  }

  @Test
  public void testGetContentDescriptionForFile() throws Exception {
    IContentDescription contentDescription = mock(IContentDescription.class);
    IFile file = mock(IFile.class);
    when(file.getContentDescription()).thenReturn(contentDescription);

    IContentDescription returnedCD = ContentTypes.getContentDescription(file);

    assertSame(contentDescription, returnedCD);
  }

  @Test
  public void testGetContentDescriptionForFileWithException() throws Exception {
    IFile file = mock(IFile.class);
    CoreException coreException = mock(CoreException.class);
    when(file.getContentDescription()).thenThrow(coreException);

    IContentDescription contentDescription = ContentTypes.getContentDescription(file);

    assertThat(contentDescription).isInstanceOf(NullContentDescription.class);
  }

  @Test
  public void testGetContentDescriptionForFileWhenNull() throws Exception {
    IFile file = mock(IFile.class);
    when(file.getContentDescription()).thenReturn(null);

    IContentDescription contentDescription = ContentTypes.getContentDescription(file);

    assertThat(contentDescription).isInstanceOf(NullContentDescription.class);
  }

  @Test
  public void testGetContentDescriptionForContent() throws Exception {
    FileEntry fileEntry = createFileEntry("name.txt");
    IContentDescription description = ContentTypes.getContentDescription(fileEntry);

    assertNotNull(description);
  }

  @Test
  public void testGetContentDescriptionForContentWithoutName() throws Exception {
    FileEntry fileEntry = createFileEntry("");
    IContentDescription description = ContentTypes.getContentDescription(fileEntry);

    assertThat(description).isInstanceOf(NullContentDescription.class);
  }

  private static FileEntry createFileEntry(String name) throws IOException {
    FileEntry result = mock(FileEntry.class);
    when(result.getName()).thenReturn(name);
    when(result.open()).thenReturn(new ByteArrayInputStream(new byte[0]));
    return result;
  }

  private static IContentType createContentType(String id) {
    IContentType result = mock(IContentType.class);
    when(result.getId()).thenReturn(id);
    return result;
  }
}
