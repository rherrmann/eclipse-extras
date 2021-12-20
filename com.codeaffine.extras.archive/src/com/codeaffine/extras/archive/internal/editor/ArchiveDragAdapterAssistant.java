package com.codeaffine.extras.archive.internal.editor;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
// import org.eclipse.ui.internal.navigator.Policy;
import org.eclipse.ui.navigator.CommonDragAdapterAssistant;

public class ArchiveDragAdapterAssistant extends CommonDragAdapterAssistant {

  // public ArchiveDragAdapterAssistant() {
  // Policy.DEBUG_DND = true;
  // }

  @Override
  public Transfer[] getSupportedTransferTypes() {
    return FileEntryDragSupport.getTransferTypes();
  }

  @Override
  public void dragStart(DragSourceEvent event, IStructuredSelection selection) {
    event.doit = false;
    if (new FileEntryDragSupport(selection).getFileEntries().length > 0) {
      event.doit = true;
    }
  }

  @Override
  public boolean setDragData(DragSourceEvent event, IStructuredSelection selection) {
    boolean result = false;
    if (FileEntryDragSupport.isTransferTypeSupported(event.dataType)) {
      event.data = new FileEntryDragSupport(selection).getEditorInputTransferData();
      result = true;
    }
    return result;
  }

  @Override
  public void dragFinished(DragSourceEvent anEvent, IStructuredSelection aSelection) {
    PersistableEditorInputFactory.clearPersistedEdiorInputs();
  }
}
