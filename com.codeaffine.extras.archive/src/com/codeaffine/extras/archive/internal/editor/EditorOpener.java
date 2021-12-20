package com.codeaffine.extras.archive.internal.editor;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import com.codeaffine.extras.archive.internal.model.FileEntry;

class EditorOpener {
  private static final int MATCH_ALL = IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID;

  private final IWorkbenchPage workbenchPage;
  private final FileEntry fileEntry;
  private final FileEntryEditor fileEntryEditor;

  EditorOpener(IWorkbenchPage workbenchPage, FileEntry fileEntry) {
    this.workbenchPage = workbenchPage;
    this.fileEntry = fileEntry;
    this.fileEntryEditor = new FileEntryEditor(fileEntry);
  }

  void open() throws PartInitException {
    IEditorDescriptor editorDescriptor = fileEntryEditor.getEditorDescriptor();
    if (editorDescriptor == null) {
      showNoEditorFoundInfo();
    } else {
      open(editorDescriptor);
    }
  }

  private void open(IEditorDescriptor editorDescriptor) throws PartInitException {
    IEditorInput editorInput = fileEntryEditor.createEditorInput();
    workbenchPage.openEditor(editorInput, editorDescriptor.getId(), true, MATCH_ALL);
  }

  private void showNoEditorFoundInfo() {
    String msg = MessageFormat.format("No editor was found to open {0}.", fileEntry.getName());
    Shell shell = workbenchPage.getWorkbenchWindow().getShell();
    MessageDialog.openInformation(shell, "Information", msg);
  }
}
