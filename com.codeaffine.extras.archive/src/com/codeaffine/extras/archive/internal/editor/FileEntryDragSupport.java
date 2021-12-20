package com.codeaffine.extras.archive.internal.editor;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.part.EditorInputTransfer.EditorInputData;
import com.codeaffine.extras.archive.internal.model.FileEntry;

class FileEntryDragSupport {

  private static final EditorInputTransfer SUPPORTED_TRANSFER_TYPE = EditorInputTransfer.getInstance();

  static Transfer[] getTransferTypes() {
    return new Transfer[] {SUPPORTED_TRANSFER_TYPE};
  }

  static boolean isTransferTypeSupported(TransferData dataType) {
    return SUPPORTED_TRANSFER_TYPE.isSupportedType(dataType);
  }

  private final FileEntry[] fileEntries;

  FileEntryDragSupport(IStructuredSelection selection) {
    fileEntries = getFileEntries(selection);
  }

  FileEntry[] getFileEntries() {
    return fileEntries;
  }

  EditorInputData[] getEditorInputTransferData() {
    EditorInputData[] result = new EditorInputData[fileEntries.length];
    for (int i = 0; i < result.length; i++) {
      FileEntry fileEntry = fileEntries[i];
      result[i] = createEditorInputTransfer(fileEntry);
    }
    return result;
  }

  private static EditorInputData createEditorInputTransfer(FileEntry fileEntry) {
    FileEntryEditor editor = new FileEntryEditor(fileEntry);
    IEditorInput editorInput = editor.createEditorInput();
    IEditorDescriptor editorDescriptor = editor.getEditorDescriptor();
    return EditorInputTransfer.createEditorInputData(editorDescriptor.getId(), editorInput);
  }

  private static FileEntry[] getFileEntries(IStructuredSelection selection) {
    Collection<FileEntry> fileEntries = new LinkedList<FileEntry>();
    Iterator<?> iterator = selection.iterator();
    while (iterator.hasNext()) {
      Object element = iterator.next();
      if (element instanceof FileEntry) {
        fileEntries.add((FileEntry) element);
      }
    }
    return fileEntries.toArray(new FileEntry[fileEntries.size()]);
  }
}
