package com.codeaffine.extras.archive.internal.editor;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class FileEntryEditorTest {

  private FileEntry fileEntry;
  private FileEntryEditor fileEntryEditor;

  @Before
  public void setUp() {
    fileEntry = mock(FileEntry.class);
    fileEntryEditor = new FileEntryEditor(fileEntry);
  }

  @Test
  public void testGetEditorDescriptorReturnsDefaultTextEditor() throws Exception {
    when(fileEntry.getName()).thenReturn("unknown.file");
    when(fileEntry.open()).thenReturn(new ByteArrayInputStream(new byte[0]));

    IEditorDescriptor editorDescriptor = fileEntryEditor.getEditorDescriptor();

    assertNotNull(editorDescriptor);
  }

  @Test
  public void testCreateEditorInput() throws Exception {
    when(fileEntry.getName()).thenReturn("unknown.file");
    when(fileEntry.open()).thenReturn(new ByteArrayInputStream(new byte[0]));

    IEditorInput editorInput = fileEntryEditor.createEditorInput();

    assertNotNull(editorInput);
  }

}
