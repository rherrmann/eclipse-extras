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
import org.junit.rules.TemporaryFolder;

import com.codeaffine.extras.ide.internal.delete.DeleteEditorFileHandler.DeleteEditorFilePrompter;
import com.codeaffine.extras.test.util.ProjectHelper;
import com.codeaffine.extras.test.util.ServiceHelper;


public class DeleteEditorFileHandler_ResourcePDETest {

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
    prompter = mock(DeleteEditorFilePrompter.class);
    handler = spy(new DeleteEditorFileHandler(prompter));
    doNothing().when(handler).deleteResource(any(), any());
  }

  @Test
  public void testEnablementWithFileEditor() throws CoreException {
    IFile file = projectHelper.createFile("file.txt", "content");
    IEvaluationContext evaluationContext = createEvaluationContext(new FileEditorInput(file));

    handler.setEnabled(evaluationContext);

    assertThat(handler.isEnabled()).isTrue();
  }

  @Test
  public void testEnablementWithFolderEditor() throws CoreException {
    IFolder folder = projectHelper.createFolder("folder");
    IEditorInput editorInput = mock(IEditorInput.class);
    when(editorInput.getAdapter(IResource.class)).thenReturn(folder);
    IEvaluationContext evaluationContext = createEvaluationContext(editorInput);

    handler.setEnabled(evaluationContext);

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testEnablementWithNonResourceEditor() {
    IEvaluationContext evaluationContext = createEvaluationContext(new NonFileEditorInput());

    handler.setEnabled(evaluationContext);

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testEnablementWithoutActiveEditor() {
    IEvaluationContext evaluationContext = createEvaluationContext(null);

    handler.setEnabled(evaluationContext);

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testEnablementWithNullEvaluationContext() {
    handler.setEnabled(new Object());

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testEnablementWithIrregularEvaluationContext() {
    handler.setEnabled(new Object());

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testEnablementWithObjectAsEditorInput() {
    IEvaluationContext evaluationContext = createEvaluationContext(new Object());

    handler.setEnabled(evaluationContext);

    assertThat(handler.isEnabled()).isFalse();
  }

  @Test
  public void testExecuteWithExistingResource() throws CoreException {
    IFile file = projectHelper.createFile("file.txt", "content");
    IEvaluationContext evaluationContext = createEvaluationContext(new FileEditorInput(file));
    ExecutionEvent event = createExecutionEvent(evaluationContext);

    handler.execute(event);

    verify(handler).deleteResource(workbenchPage.getWorkbenchWindow(), file);
  }

  @Test
  public void testExecuteWithNonExistingResource() throws CoreException {
    IFile file = projectHelper.createFile("file.txt", "content");
    file.delete(true, new NullProgressMonitor());
    IEvaluationContext evaluationContext = createEvaluationContext(new FileEditorInput(file));
    ExecutionEvent event = createExecutionEvent(evaluationContext);

    handler.execute(event);

    verify(handler, never()).deleteResource(any(), any());
  }

  @Test
  public void testExecuteWithNonResourceEditor() {
    IEvaluationContext evaluationContext = createEvaluationContext(new NonFileEditorInput());
    ExecutionEvent event = createExecutionEvent(evaluationContext);

    handler.execute(event);

    verify(handler, never()).deleteResource(any(), any());
  }

  private ExecutionEvent createExecutionEvent(IEvaluationContext evaluationContext) {
    return new ExecutionEvent(getDeleteEditorFileCommand(), emptyMap(), null, evaluationContext);
  }

  private Command getDeleteEditorFileCommand() {
    IWorkbench workbench = workbenchPage.getWorkbenchWindow().getWorkbench();
    ICommandService commandService = ServiceHelper.getService(workbench, ICommandService.class);
    return commandService.getCommand(DeleteEditorFileHandler.COMMAND_ID);
  }

  private IEvaluationContext createEvaluationContext(Object editorInput) {
    IWorkbenchWindow activeWorkbenchWindow = workbenchPage.getWorkbenchWindow();
    IEvaluationContext result = new EvaluationContext(null, new Object());
    result.addVariable(ISources.ACTIVE_WORKBENCH_WINDOW_NAME, activeWorkbenchWindow);
    if (editorInput != null) {
      result.addVariable(ISources.ACTIVE_EDITOR_INPUT_NAME, editorInput);
    }
    return result;
  }

  private static class NonFileEditorInput implements IEditorInput {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object getAdapter(Class adapter) {
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
