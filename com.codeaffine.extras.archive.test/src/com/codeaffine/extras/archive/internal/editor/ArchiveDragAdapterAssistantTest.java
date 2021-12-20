package com.codeaffine.extras.archive.internal.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.part.EditorInputTransfer.EditorInputData;
import org.junit.Before;
import org.junit.Test;
import com.codeaffine.extras.archive.internal.model.FileEntry;

public class ArchiveDragAdapterAssistantTest {

  private ArchiveDragAdapterAssistant dragAssistant;
  private DragSourceEvent event;

  @Test
  public void testGetSupportedTransferTypes() throws Exception {
    Transfer[] supportedTransferTypes = dragAssistant.getSupportedTransferTypes();

    assertEquals(1, supportedTransferTypes.length);
    assertEquals(EditorInputTransfer.getInstance().getClass(), supportedTransferTypes[0].getClass());
  }

  @Test
  public void testDragStartWithSingleFileEntry() throws Exception {
    DragSourceEvent event = mock(DragSourceEvent.class);

    dragAssistant.dragStart(event, new StructuredSelection(mock(FileEntry.class)));

    assertTrue(event.doit);
  }

  @Test
  public void testDragStartWithSingleObject() throws Exception {
    DragSourceEvent event = mock(DragSourceEvent.class);

    dragAssistant.dragStart(event, new StructuredSelection(new Object()));

    assertFalse(event.doit);
  }

  @Test
  public void testDragStartWithMultipleFileEntries() throws Exception {
    DragSourceEvent event = mock(DragSourceEvent.class);

    Object[] elements = new Object[] {mock(FileEntry.class), mock(FileEntry.class)};
    dragAssistant.dragStart(event, new StructuredSelection(elements));

    assertTrue(event.doit);
  }

  @Test
  public void testDragStartWithMixedFileEntriesAndObjects() throws Exception {

    Object[] elements = new Object[] {new Object(), mock(FileEntry.class)};
    dragAssistant.dragStart(event, new StructuredSelection(elements));

    assertTrue(event.doit);
  }

  @Test
  public void testSetDragData() throws Exception {
    event.dataType = EditorInputTransfer.getInstance().getSupportedTypes()[0];
    IStructuredSelection selection = new StructuredSelection(createFileEntry());

    boolean setDragData = dragAssistant.setDragData(event, selection);

    assertTrue(setDragData);
    assertNotNull(event.data);
    assertEquals(EditorInputData[].class, event.data.getClass());
  }

  @Before
  public void setUp() {
    dragAssistant = new ArchiveDragAdapterAssistant();
    event = mock(DragSourceEvent.class);
    event.doit = true; // emulate behavior of CommonDragAdapter#dragStart()
  }

  private static FileEntry createFileEntry() throws IOException {
    FileEntry result = mock(FileEntry.class);
    when(result.open()).thenReturn(new ByteArrayInputStream(new byte[0]));
    return result;
  }
}
