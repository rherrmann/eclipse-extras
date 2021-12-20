package com.codeaffine.extras.archive.internal.extract;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import com.codeaffine.extras.archive.internal.util.Images;

public class ExtractEntryActionProvider extends CommonActionProvider {

  private ExtractEntryAction extractEntryAction;

  @Override
  public void init(ICommonActionExtensionSite extensionSite) {
    createExtractEntryAction(extensionSite.getViewSite().getShell());
  }

  @Override
  public void fillContextMenu(IMenuManager menuManager) {
    if (extractEntryAction != null && !getContext().getSelection().isEmpty()) {
      IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
      extractEntryAction.selectionChanged(selection);
      menuManager.insertAfter(ICommonMenuConstants.GROUP_EDIT, extractEntryAction);
    }
  }

  private void createExtractEntryAction(Shell shell) {
    extractEntryAction = new ExtractEntryAction(shell, "E&xtract...");
    extractEntryAction.setImageDescriptor(Images.EXTRACT_ACTION);
  }
}
