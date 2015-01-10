package com.codeaffine.extras.platform.internal.openwith;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.platform.test.ProjectHelper;


public class OpenWithQuickMenuPDETest {

  private IWorkbench workbench;
  private ProjectHelper projectHelper;
  private OpenWithQuickMenuHandler handler;

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
  public void testEnablementWithSingleFile() throws CoreException {
    IFile file = projectHelper.createFile( "file.txt", "content" );

    handler.setEnabled( createEvaluationContext( file ) );

    assertThat( handler.isEnabled() ).isTrue();
  }

  @Test
  public void testEnablementWithMultipleFiles() throws CoreException {
    IFile file1 = projectHelper.createFile( "file1.txt", "content" );
    IFile file2 = projectHelper.createFile( "file2.txt", "content" );

    handler.setEnabled( createEvaluationContext( file1, file2 ) );

    assertThat( handler.isEnabled() ).isFalse();
  }

  @Test
  public void testEnablementWithEmptySelection() {
    handler.setEnabled( createEvaluationContext( StructuredSelection.EMPTY ) );

    assertThat( handler.isEnabled() ).isFalse();
  }

  @Test
  public void testEnablementWithNonStructuredSelection() {
    handler.setEnabled( createEvaluationContext( mock( ISelection.class ) ) );

    assertThat( handler.isEnabled() ).isFalse();
  }

  @Test
  public void testExecute() throws CoreException {
    IFile file = projectHelper.createFile( "file.txt", "content" );
    Command command = getOpenWithQuickMenuCommand();
    IEvaluationContext evaluationContext = createEvaluationContext( file );
    ExecutionEvent event = new ExecutionEvent( command, emptyMap(), null, evaluationContext );

    Object executionResult = handler.execute( event );

    assertThat( executionResult ).isNull();
  }

  @Before
  public void setUp() {
    workbench = PlatformUI.getWorkbench();
    projectHelper = new ProjectHelper();
    handler = new OpenWithQuickMenuHandler();
  }

  @After
  public void tearDown() throws CoreException {
    projectHelper.dispose();
  }

  private Command getOpenWithQuickMenuCommand() {
    ICommandService commandService = ( ICommandService )workbench.getService( ICommandService.class );
    return commandService.getCommand( OpenWithQuickMenuHandler.COMMAND_ID );
  }

  private IEvaluationContext createEvaluationContext( Object... selectedElements ) {
    ISelection selection = new StructuredSelection( selectedElements );
    return createEvaluationContext( selection );
  }

  private IEvaluationContext createEvaluationContext( ISelection selection ) {
    IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
    IEvaluationContext result = new EvaluationContext( null, new Object() );
    result.addVariable( ISources.ACTIVE_WORKBENCH_WINDOW_NAME, activeWorkbenchWindow );
    result.addVariable( ISources.ACTIVE_CURRENT_SELECTION_NAME, selection );
    return result;
  }
}
