package com.codeaffine.extras.archive.internal.editor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public class OpenActionProvider extends CommonActionProvider {
  private OpenAction openAction;

  @Override
  public void init(ICommonActionExtensionSite extensionSite) {
    if (extensionSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
      ICommonViewerWorkbenchSite site = (ICommonViewerWorkbenchSite) extensionSite.getViewSite();
      openAction = new OpenAction(site.getPage(), "Open");
    }
  }

  @Override
  public void setContext(ActionContext context) {
    super.setContext(context);
    if (openAction != null && hasSelection(context)) {
      openAction.selectionChanged((IStructuredSelection) context.getSelection());
    }
  }

  @Override
  public void fillActionBars(IActionBars actionBars) {
    if (openAction != null && openAction.isEnabled()) {
      actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
    }
  }

  @Override
  public void fillContextMenu(IMenuManager menuManager) {
    if (openAction != null && getContext() != null && !getContext().getSelection().isEmpty()) {
      IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
      openAction.selectionChanged(selection);
      menuManager.insertAfter(ICommonMenuConstants.GROUP_OPEN, openAction);
    }
  }

  private static boolean hasSelection(ActionContext context) {
    return context != null && context.getSelection() instanceof IStructuredSelection;
  }
}
