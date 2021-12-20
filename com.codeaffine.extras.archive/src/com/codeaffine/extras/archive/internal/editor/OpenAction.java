package com.codeaffine.extras.archive.internal.editor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import com.codeaffine.extras.archive.internal.model.FileEntry;
import com.codeaffine.extras.archive.internal.util.StatusUtil;

public class OpenAction extends BaseSelectionListenerAction {

  private final IWorkbenchPage page;

  public OpenAction(IWorkbenchPage page, String text) {
    super(text);
    this.page = page;
  }

  @Override
  public void run() {
    try {
      for (FileEntry fileEntry : getSelectedFileEntries(getStructuredSelection())) {
        new EditorOpener(page, fileEntry).open();
      }
    } catch (PartInitException pie) {
      StatusUtil.show(pie);
    }
  }

  @Override
  protected boolean updateSelection(IStructuredSelection selection) {
    return getSelectedFileEntries(selection).length > 0;
  }

  private static FileEntry[] getSelectedFileEntries(IStructuredSelection selection) {
    List<FileEntry> fileEntries = new LinkedList<FileEntry>();
    Iterator<?> iterator = selection.iterator();
    while (iterator.hasNext()) {
      Object element = iterator.next();
      if (element instanceof FileEntry) {
        fileEntries.add((FileEntry) element);
      }
    }
    return fileEntries.toArray(FileEntry[]::new);
  }
}
