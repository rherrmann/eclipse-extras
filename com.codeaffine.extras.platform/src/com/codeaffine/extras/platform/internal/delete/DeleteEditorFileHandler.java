package com.codeaffine.extras.platform.internal.delete;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.ResourceUtil;


public class DeleteEditorFileHandler extends AbstractHandler {

  public static final String COMMAND_ID
    = "com.codeaffine.extras.platform.internal.DeleteEditorFileCommand";

  @Override
  public Object execute( ExecutionEvent event ) {
    IEditorInput editorInput = HandlerUtil.getActiveEditorInput( event );
    IFile file = ResourceUtil.getFile( editorInput );
    if( isDeletable( file ) ) {
      deleteFile( HandlerUtil.getActiveWorkbenchWindow( event ), file );
    }
    return null;
  }

  @Override
  public void setEnabled( Object evaluationContext ) {
    if( evaluationContext instanceof IEvaluationContext ) {
      setBaseEnabled( isEnabled( ( IEvaluationContext )evaluationContext ) );
    } else {
      setBaseEnabled( false );
    }
  }

  protected void deleteFile( IShellProvider shellProvier, IFile file ) {
    DeleteResourceAction deleteAction = new DeleteResourceAction( shellProvier );
    deleteAction.selectionChanged( new StructuredSelection( file ) );
    deleteAction.run();
  }

  private static boolean isDeletable( IFile file ) {
    return file != null && file.isAccessible();
  }

  private static boolean isEnabled( IEvaluationContext evaluationContext ) {
    Object variable = evaluationContext.getVariable( ISources.ACTIVE_EDITOR_INPUT_NAME );
    return ResourceUtil.getFile( ( IEditorInput )variable ) != null;
  }

}
