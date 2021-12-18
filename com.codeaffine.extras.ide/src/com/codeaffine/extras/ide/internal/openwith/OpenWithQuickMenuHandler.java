package com.codeaffine.extras.ide.internal.openwith;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.ResourceUtil;

public class OpenWithQuickMenuHandler extends AbstractHandler {

  public static final String COMMAND_ID = "com.codeaffine.extras.ide.internal.OpenWithQuickMenu";

  @Override
  public Object execute(ExecutionEvent event) {
    IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
    IFile file = extractFileFromSelection(getStructuredSelection(event));
    showMenu(workbenchWindow.getActivePage(), file);
    return null;
  }

  @Override
  public void setEnabled(Object evaluationContext) {
    if (evaluationContext instanceof IEvaluationContext) {
      setEnabled((IEvaluationContext) evaluationContext);
    } else {
      setBaseEnabled(false);
    }
  }

  protected void showMenu(IWorkbenchPage workbenchPage, IFile file) {
    new OpenWithQuickMenu(workbenchPage, file).show();
  }

  private void setEnabled(IEvaluationContext evaluationContext) {
    IStructuredSelection selection = getSelection(evaluationContext);
    setBaseEnabled(isEnabled(selection));
  }

  private static boolean isEnabled(IStructuredSelection selection) {
    return extractFileFromSelection(selection) != null;
  }

  private static IStructuredSelection getSelection(IEvaluationContext evaluationContext) {
    IStructuredSelection result = StructuredSelection.EMPTY;
    Object variable = evaluationContext.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
    if (variable instanceof IStructuredSelection) {
      result = (IStructuredSelection) variable;
    }
    return result;
  }

  private static IStructuredSelection getStructuredSelection(ExecutionEvent event) {
    IStructuredSelection result = StructuredSelection.EMPTY;
    ISelection selection = HandlerUtil.getCurrentSelection(event);
    if (selection instanceof IStructuredSelection) {
      result = (IStructuredSelection) selection;
    }
    return result;
  }

  private static IFile extractFileFromSelection(IStructuredSelection selection) {
    IFile result = null;
    if (selection.size() == 1) {
      result = ResourceUtil.getFile(selection.getFirstElement());
    }
    return result;
  }
}
