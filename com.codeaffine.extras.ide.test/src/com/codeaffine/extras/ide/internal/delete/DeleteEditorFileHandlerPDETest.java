package com.codeaffine.extras.ide.internal.delete;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.test.util.ProjectHelper;


public class DeleteEditorFileHandlerPDETest {

  @Rule
  public ProjectHelper projectHelper = new ProjectHelper();

  private IWorkbenchPage workbenchPage;
  private DeleteEditorFileHandler handler;

  @Test
  public void testEnablementWithFileEditor() throws CoreException {
    IFile file = projectHelper.createFile( "file.txt", "content" );
    IEvaluationContext evaluationContext = createEvaluationContext( new FileEditorInput( file ) );

    handler.setEnabled( evaluationContext );

    assertThat( handler.isEnabled() ).isTrue();
  }

  @Test
  public void testEnablementWithFolderEditor() throws CoreException {
    IFolder folder = projectHelper.createFolder( "folder" );
    IEditorInput editorInput = mock( IEditorInput.class );
    when( editorInput.getAdapter( IResource.class ) ).thenReturn( folder );
    IEvaluationContext evaluationContext = createEvaluationContext( editorInput );

    handler.setEnabled( evaluationContext );

    assertThat( handler.isEnabled() ).isFalse();
  }

  @Test
  public void testEnablementWithNonResourceEditor() {
    IEvaluationContext evaluationContext = createEvaluationContext( new NonFileEditorInput() );

    handler.setEnabled( evaluationContext );

    assertThat( handler.isEnabled() ).isFalse();
  }

  @Test
  public void testEnablementWithoutActiveEditor() {
    IEvaluationContext evaluationContext = createEvaluationContext( null );

    handler.setEnabled( evaluationContext );

    assertThat( handler.isEnabled() ).isFalse();
  }

  @Test
  public void testEnablementWithNullEvaluationContext() {
    handler.setEnabled( new Object() );

    assertThat( handler.isEnabled() ).isFalse();
  }

  @Test
  public void testEnablementWithIrregularEvaluationContext() {
    handler.setEnabled( new Object() );

    assertThat( handler.isEnabled() ).isFalse();
  }

  @Test
  public void testEnablementWithObjectAsEditorInput() {
    IEvaluationContext evaluationContext = createEvaluationContext( new Object() );

    handler.setEnabled( evaluationContext );

    assertThat( handler.isEnabled() ).isFalse();
  }

  @Test
  public void testExecuteWithFileEditor() throws CoreException {
    IFile file = projectHelper.createFile( "file.txt", "content" );
    IEvaluationContext evaluationContext = createEvaluationContext( new FileEditorInput( file ) );
    ExecutionEvent event = createExecutionEvent( evaluationContext );

    handler.execute( event );

    verify( handler ).deleteFile( workbenchPage.getWorkbenchWindow(), file );
  }

  @Test
  public void testExecuteWithNonExistingFile() throws CoreException {
    IFile file = projectHelper.createFile( "file.txt", "content" );
    file.delete( true, new NullProgressMonitor() );
    IEvaluationContext evaluationContext = createEvaluationContext( new FileEditorInput( file ) );
    ExecutionEvent event = createExecutionEvent( evaluationContext );

    handler.execute( event );

    verify( handler, never() ).deleteFile( any( IShellProvider.class ), any( IFile.class ) );
  }

  @Test
  public void testExecuteWithNonResourceEditor() {
    IEvaluationContext evaluationContext = createEvaluationContext( new NonFileEditorInput() );
    ExecutionEvent event = createExecutionEvent( evaluationContext );

    handler.execute( event );

    verify( handler, never() ).deleteFile( any( IShellProvider.class ), any( IFile.class ) );
  }

  @Before
  public void setUp() {
    workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    handler = spy( new DeleteEditorFileHandler() );
    doNothing().when( handler ).deleteFile( any( IShellProvider.class ), any( IFile.class ) );
  }

  private ExecutionEvent createExecutionEvent( IEvaluationContext evaluationContext ) {
    return new ExecutionEvent( getDeleteEditorFileCommand(), emptyMap(), null, evaluationContext );
  }

  private Command getDeleteEditorFileCommand() {
    IWorkbench workbench = workbenchPage.getWorkbenchWindow().getWorkbench();
    ICommandService commandService = workbench.getService( ICommandService.class );
    return commandService.getCommand( DeleteEditorFileHandler.COMMAND_ID );
  }

  private IEvaluationContext createEvaluationContext( Object editorInput ) {
    IWorkbenchWindow activeWorkbenchWindow = workbenchPage.getWorkbenchWindow();
    IEvaluationContext result = new EvaluationContext( null, new Object() );
    result.addVariable( ISources.ACTIVE_WORKBENCH_WINDOW_NAME, activeWorkbenchWindow );
    if( editorInput != null ) {
      result.addVariable( ISources.ACTIVE_EDITOR_INPUT_NAME, editorInput );
    }
    return result;
  }

  private static class NonFileEditorInput implements IEditorInput {
    @Override
    public <T> T getAdapter( Class<T> adapter ) {
      return null;
    }

    @Override
    public boolean exists() {
      return false;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
      return null;
    }

    @Override
    public String getName() {
      return "test-editor-input";
    }

    @Override
    public IPersistableElement getPersistable() {
      return null;
    }

    @Override
    public String getToolTipText() {
      return "";
    }

  }
}
