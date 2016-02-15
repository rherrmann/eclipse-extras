package com.codeaffine.extras.ide.internal.delete;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.codeaffine.extras.ide.internal.delete.DeleteEditorFileHandler.DeleteEditorFilePrompter;
import com.codeaffine.extras.ide.test.ServiceHelper;
import com.codeaffine.extras.test.util.ProjectHelper;


public class DeleteEditorFileHandler_FilePDETest {

  @Rule
  public ProjectHelper projectHelper = new ProjectHelper();
  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  private IWorkbenchPage workbenchPage;
  private DeleteEditorFilePrompter prompter;
  private DeleteEditorFileHandler handler;

  @Before
  public void setUp() {
    workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    prompter = mock( DeleteEditorFilePrompter.class );
    when( prompter.confirmDelete( any(), any() ) ).thenReturn( true );
    handler = spy( new DeleteEditorFileHandler( prompter ) );
    doNothing().when( handler ).deleteResource( any(), any() );
  }

  @After
  public void tearDown() {
    workbenchPage.closeAllEditors( false );
  }

  @Test
  public void testEnablement() throws Exception {
    File file = tempFolder.newFile( "foo.txt" );
    IEditorPart editor = openEditor( file );
    IEvaluationContext evaluationContext = createEvaluationContext( editor.getEditorInput() );

    handler.setEnabled( evaluationContext );

    assertThat( handler.isEnabled() ).isTrue();
  }

  @Test
  public void testEnablementWithPathEditorInput() throws IOException {
    File file = tempFolder.newFile( "foo.txt" );
    IEvaluationContext evaluationContext = createEvaluationContext( mockPathEditorInput( file ) );

    handler.setEnabled( evaluationContext );

    assertThat( handler.isEnabled() ).isTrue();
  }

  @Test
  public void testEnablementWithUriEditorInput() throws IOException {
    File file = tempFolder.newFile( "foo.txt" );
    IEvaluationContext evaluationContext = createEvaluationContext( mockUriEditorInput( file ) );

    handler.setEnabled( evaluationContext );

    assertThat( handler.isEnabled() ).isTrue();
  }

  @Test
  public void testExecute() throws Exception {
    File file = tempFolder.newFile( "foo.txt" );
    IEditorPart editor = openEditor( file );

    executeHandler( editor.getEditorInput() );

    assertThat( file ).doesNotExist();
    assertThat( workbenchPage.findEditor( editor.getEditorInput() ) ).isNull();
  }

  @Test
  public void testExecuteWithDeletedFile() throws Exception {
    File file = tempFolder.newFile( "foo.txt" );
    IEditorPart editor = openEditor( file );
    file.delete();

    executeHandler( editor.getEditorInput() );

    verify( prompter ).showError( any(), any() );
  }

  @Test
  public void testExecuteWithCancelledConfirmation() throws Exception {
    when( prompter.confirmDelete( any(), any() ) ).thenReturn( false );
    File file = tempFolder.newFile( "foo.txt" );
    IEditorPart editor = openEditor( file );

    executeHandler( editor.getEditorInput() );

    assertThat( file ).exists();
    assertThat( workbenchPage.findEditor( editor.getEditorInput() ) ).isEqualTo( editor );
  }

  @Test
  public void testExecuteWithUriEditorInput() throws IOException {
    File file = tempFolder.newFile( "foo.txt" );
    IURIEditorInput editorInput = mockUriEditorInput( file );

    executeHandler( editorInput );

    assertThat( file ).doesNotExist();
  }

  @Test
  public void testExecuteWithPathEditorInput() throws IOException {
    File file = tempFolder.newFile( "foo.txt" );
    IPathEditorInput editorInput = mockPathEditorInput( file );

    executeHandler( editorInput );

    assertThat( file ).doesNotExist();
  }

  @Test
  public void testConfirmDeletePrompter() throws Exception {
    File file = tempFolder.newFile( "foo.txt" );
    IEditorPart editor = openEditor( file );
    when( prompter.confirmDelete( any(), any() ) ).thenReturn( false );

    executeHandler( editor.getEditorInput() );

    verify( prompter ).confirmDelete( workbenchPage.getWorkbenchWindow(), file );
  }

  private static IPathEditorInput mockPathEditorInput( File file ) throws IOException {
    IPathEditorInput editorInput = mock( IPathEditorInput.class );
    when( editorInput.getPath() ).thenReturn( new Path( file.getCanonicalPath() ) );
    return editorInput;
  }

  private static IURIEditorInput mockUriEditorInput( File file ) {
    IURIEditorInput editorInput = mock( IURIEditorInput.class );
    when( editorInput.getURI() ).thenReturn( file.toURI() );
    return editorInput;
  }

  private void executeHandler( IEditorInput editorInput ) {
    IEvaluationContext evaluationContext = createEvaluationContext( editorInput );
    ExecutionEvent event = createExecutionEvent( evaluationContext );
    handler.execute( event );
  }

  private IEditorPart openEditor( File file ) throws PartInitException {
    String editorId = IDE.getEditorDescriptor( file.getName() ).getId();
    return IDE.openEditor( workbenchPage, file.toURI(), editorId, true );
  }

  private ExecutionEvent createExecutionEvent( IEvaluationContext evaluationContext ) {
    return new ExecutionEvent( getDeleteEditorFileCommand(), emptyMap(), null, evaluationContext );
  }

  private Command getDeleteEditorFileCommand() {
    IWorkbench workbench = workbenchPage.getWorkbenchWindow().getWorkbench();
    ICommandService commandService = ServiceHelper.getService( workbench, ICommandService.class );
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
}
