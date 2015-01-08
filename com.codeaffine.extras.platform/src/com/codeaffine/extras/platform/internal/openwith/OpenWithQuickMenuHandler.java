package com.codeaffine.extras.platform.internal.openwith;

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

  public static final String COMMAND_ID
    = "com.codeaffine.extras.platform.internal.OpenWithQuickMenu";

  @Override
  public Object execute( ExecutionEvent event ) {
    IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow( event );
    IFile file = extractFileFromSelection( HandlerUtil.getCurrentSelection( event ) );
    showMenu( workbenchWindow.getActivePage(), file );
    return null;
  }

  @Override
  public void setEnabled( Object evaluationContext ) {
    if( evaluationContext instanceof IEvaluationContext ) {
      setEnabled( ( IEvaluationContext )evaluationContext );
    } else {
      setBaseEnabled( false );
    }
  }

  private void setEnabled( IEvaluationContext evaluationContext ) {
    ISelection selection = getSelection( evaluationContext );
    setBaseEnabled( isEnabled( selection ) );
  }

  private static boolean isEnabled( ISelection selection ) {
    return extractFileFromSelection( selection ) != null;
  }

  private static ISelection getSelection( IEvaluationContext evaluationContext ) {
    ISelection result = StructuredSelection.EMPTY;
    Object variable = evaluationContext.getVariable( ISources.ACTIVE_CURRENT_SELECTION_NAME );
    if( variable instanceof ISelection ) {
      result = ( ISelection )variable;
    }
    return result;
  }

  private static IFile extractFileFromSelection( ISelection selection ) {
    IFile result = null;
    if( selection instanceof IStructuredSelection ) {
      IStructuredSelection structuredSelection = ( IStructuredSelection )selection;
      if( structuredSelection.size() == 1 ) {
        IFile file = ResourceUtil.getFile( structuredSelection.getFirstElement() );
        result = file;
      }
    }
    return result;
  }

  private static void showMenu( IWorkbenchPage workbenchPage, IFile file ) {
    new OpenWithQuickMenu( workbenchPage, file ).show();
  }
}
