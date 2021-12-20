package com.codeaffine.extras.archive.internal.extract;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import com.codeaffine.extras.archive.internal.model.ArchiveEntry;

public class ExtractEntryAction extends BaseSelectionListenerAction {
  private final Shell shell;

  public ExtractEntryAction(Shell shell, String string) {
    super(string);
    this.shell = shell;
  }

  @Override
  protected boolean updateSelection(IStructuredSelection selection) {
    return !selection.isEmpty();
  }

  @Override
  public void run() {
    ExtractLocation extractLocation = ExtractDialog.chooseLocation(shell);
    if (extractLocation != null) {
      extract(extractLocation);
    }
  }

  private void extract(ExtractLocation extractLocation) {
    ArchiveEntry[] archiveEntries = getSelectedArchiveEntries();
    ArchiveExtractorJob job = new ArchiveExtractorJob(archiveEntries, extractLocation);
    job.setUser(true);
    job.schedule();
  }

  private ArchiveEntry[] getSelectedArchiveEntries() {
    IStructuredSelection selection = getStructuredSelection();
    ArchiveEntry[] result = new ArchiveEntry[selection.size()];
    Object[] array = selection.toArray();
    for (int i = 0; i < array.length; i++) {
      result[i] = (ArchiveEntry) array[i];
    }
    return result;
  }
}
