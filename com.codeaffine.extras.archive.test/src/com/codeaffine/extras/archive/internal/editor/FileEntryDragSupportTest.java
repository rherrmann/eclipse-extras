package com.codeaffine.extras.archive.internal.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.part.EditorInputTransfer.EditorInputData;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class FileEntryDragSupportTest {

  @Test
  public void testGetFileEntriesWithObjectSelection() throws Exception {
    FileEntryDragSupport fileEntryDragSupport = createFileEntryDragSuport(new Object());

    FileEntry[] fileEntries = fileEntryDragSupport.getFileEntries();

    assertEquals(0, fileEntries.length);
  }

  @Test
  public void testGetFileEntriesWithFileEntrySelection() throws Exception {
    FileEntryDragSupport fileEntryDragSupport = createFileEntryDragSuport(mock(FileEntry.class));

    FileEntry[] fileEntries = fileEntryDragSupport.getFileEntries();

    assertEquals(1, fileEntries.length);
  }

  @Test
  public void testGetFileEntriesWithMixedSelection() throws Exception {
    Object[] selectedElements = new Object[] {new Object(), mock(FileEntry.class)};
    FileEntryDragSupport fileEntryDragSupport = createFileEntryDragSuport(selectedElements);

    FileEntry[] fileEntries = fileEntryDragSupport.getFileEntries();

    assertEquals(1, fileEntries.length);
  }

  @Test
  public void testGetEditorInputTransferDataWithObjectSelection() throws Exception {
    FileEntryDragSupport fileEntryDragSupport = createFileEntryDragSuport(new Object());

    EditorInputData[] editorInputTransferData = fileEntryDragSupport.getEditorInputTransferData();

    assertEquals(0, editorInputTransferData.length);
  }

  @Test
  public void testGetEditorInputTransferDataWithFileEntrySelection() throws Exception {
    FileEntryDragSupport fileEntryDragSupport = createFileEntryDragSuport(createFileEntry());

    EditorInputData[] editorInputTransferData = fileEntryDragSupport.getEditorInputTransferData();

    assertEquals(1, editorInputTransferData.length);
  }

  @Test
  public void testGetEditorInputTransferDataWithMixedSelection() throws Exception {
    Object[] selectedElements = new Object[] {new Object(), createFileEntry()};
    FileEntryDragSupport fileEntryDragSupport = createFileEntryDragSuport(selectedElements);

    EditorInputData[] editorInputTransferData = fileEntryDragSupport.getEditorInputTransferData();

    assertEquals(1, editorInputTransferData.length);
  }

  @Test
  public void testIsTransferTypeSupported() throws Exception {
    TransferData dataType = FileEntryDragSupport.getTransferTypes()[0].getSupportedTypes()[0];

    boolean transferTypeSupported = FileEntryDragSupport.isTransferTypeSupported(dataType);

    assertTrue(transferTypeSupported);
  }

  private static FileEntryDragSupport createFileEntryDragSuport(Object... selectedElements) {
    IStructuredSelection selection = new StructuredSelection(selectedElements);
    FileEntryDragSupport result = new FileEntryDragSupport(selection);
    return result;
  }

  private static FileEntry createFileEntry() throws IOException {
    FileEntry result = mock(FileEntry.class);
    when(result.getName()).thenReturn("file.txt");
    when(result.open()).thenReturn(new ByteArrayInputStream(new byte[0]));
    return result;
  }
}
