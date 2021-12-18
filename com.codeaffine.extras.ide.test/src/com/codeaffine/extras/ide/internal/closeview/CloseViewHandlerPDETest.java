package com.codeaffine.extras.ide.internal.closeview;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.ide.internal.delete.DeleteEditorFileHandler;
import com.codeaffine.extras.ide.test.TestEditorInput;
import com.codeaffine.extras.ide.test.TestEditorPart;
import com.codeaffine.extras.ide.test.TestViewPart;
import com.codeaffine.extras.test.util.ServiceHelper;

public class CloseViewHandlerPDETest {

  private IWorkbenchPage workbenchPage;
  private CloseViewHandler handler;

  @Test
  public void testSetEnabledWithActiveView() throws PartInitException {
    IViewPart view = openView();

    handler.setEnabled(createEvaluationContext(view));

    assertThat(handler.isEnabled()).isTrue();
  }

  @Test
  public void testSetEnabledWithActiveEditor() throws PartInitException {
    IEditorPart editor = openEditor();

    handler.setEnabled(createEvaluationContext(editor));

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testSetEnabledWithoutActivePart() {
    handler.setEnabled(createEvaluationContext(null));

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testSetEnabledWithNullArgument() {
    handler.setEnabled(null);

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testSetEnabledWithNonEvaluationContext() {
    handler.setEnabled(new Object());

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testExecuteWithActiveView() throws PartInitException {
    IViewPart view = openView();

    handler.execute(createExecutionEvent(createEvaluationContext(view)));

    assertThat(workbenchPage.findView(TestViewPart.ID)).isNull();
  }

  @Test
  public void testExecuteWithActiveEditor() throws PartInitException {
    IEditorPart editor = openEditor();

    handler.execute(createExecutionEvent(createEvaluationContext(editor)));

    assertThat(workbenchPage.getEditorReferences()).hasSize(1);
  }

  @Test
  public void testExecuteWithoutActivePart() {
    ExecutionEvent executionEvent = createExecutionEvent(createEvaluationContext(null));

    Object executeResult = handler.execute(executionEvent);

    assertThat(executeResult).isNull();
  }

  @Before
  public void setUp() {
    workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    handler = new CloseViewHandler();
  }

  @After
  public void tearDown() {
    closeEditors();
    hideView();
  }

  private IViewPart openView() throws PartInitException {
    return workbenchPage.showView(TestViewPart.ID);
  }

  private IEditorPart openEditor() throws PartInitException {
    return IDE.openEditor(workbenchPage, new TestEditorInput(), TestEditorPart.ID);
  }

  private void closeEditors() {
    workbenchPage.closeAllEditors(false);
  }

  private void hideView() {
    IViewPart view = workbenchPage.findView(TestViewPart.ID);
    if (view != null) {
      workbenchPage.hideView(view);
    }
  }

  private IEvaluationContext createEvaluationContext(IWorkbenchPart activePart) {
    IWorkbenchWindow activeWorkbenchWindow = workbenchPage.getWorkbenchWindow();
    IEvaluationContext result = new EvaluationContext(null, new Object());
    result.addVariable(ISources.ACTIVE_WORKBENCH_WINDOW_NAME, activeWorkbenchWindow);
    if (activePart != null) {
      result.addVariable(ISources.ACTIVE_PART_NAME, activePart);
    }
    return result;
  }

  private ExecutionEvent createExecutionEvent(IEvaluationContext evaluationContext) {
    return new ExecutionEvent(getDeleteEditorFileCommand(), emptyMap(), null, evaluationContext);
  }

  private Command getDeleteEditorFileCommand() {
    IWorkbench workbench = workbenchPage.getWorkbenchWindow().getWorkbench();
    ICommandService commandService = ServiceHelper.getService(workbench, ICommandService.class);
    return commandService.getCommand(DeleteEditorFileHandler.COMMAND_ID);
  }

}
