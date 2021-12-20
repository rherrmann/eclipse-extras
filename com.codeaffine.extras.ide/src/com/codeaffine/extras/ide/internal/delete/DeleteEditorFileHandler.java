package com.codeaffine.extras.ide.internal.delete;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.ResourceUtil;


public class DeleteEditorFileHandler extends AbstractHandler {

  public interface DeleteEditorFilePrompter {
    boolean confirmDelete(IShellProvider shellProvider, File file);

    void showError(IShellProvider shellProvider, String message);
  }

  public static final String COMMAND_ID = "com.codeaffine.extras.ide.internal.DeleteEditorFileCommand";

  private final DeleteEditorFilePrompter prompter;

  public DeleteEditorFileHandler() {
    this(new DefaultDeleteEditorFilePrompter());
  }

  public DeleteEditorFileHandler(DeleteEditorFilePrompter prompter) {
    this.prompter = prompter;
  }

  @Override
  public Object execute(ExecutionEvent event) {
    IEditorInput editorInput = HandlerUtil.getActiveEditorInput(event);
    IFile resource = ResourceUtil.getFile(editorInput);
    if (resource != null) {
      if (resource.isAccessible()) {
        deleteResource(HandlerUtil.getActiveWorkbenchWindow(event), resource);
      }
    } else {
      File file = getFile(editorInput);
      IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
      if (file != null && prompter.confirmDelete(workbenchWindow, file)) {
        deleteFile(workbenchWindow, file);
      }
    }
    return null;
  }

  @Override
  public void setEnabled(Object evaluationContext) {
    if (evaluationContext instanceof IEvaluationContext) {
      setBaseEnabled(isEnabled((IEvaluationContext) evaluationContext));
    } else {
      setBaseEnabled(false);
    }
  }

  protected void deleteResource(IShellProvider shellProvier, IFile file) {
    DeleteResourceAction deleteAction = new DeleteResourceAction(shellProvier);
    deleteAction.selectionChanged(new StructuredSelection(file));
    deleteAction.run();
  }

  private void deleteFile(IWorkbenchWindow workbenchWindow, File file) {
    if (file.delete()) {
      closeEditors(workbenchWindow.getWorkbench(), file);
    } else {
      String message = MessageFormat.format("The file ''{0}'' could not be deleted.", file.getName());
      prompter.showError(workbenchWindow, message);
    }
  }

  private static void closeEditors(IWorkbench workbench, File file) {
    for (IWorkbenchWindow workbenchWindow : workbench.getWorkbenchWindows()) {
      closeEditors(workbenchWindow, file);
    }
  }

  private static void closeEditors(IWorkbenchWindow workbenchWindow, File file) {
    for (IEditorReference editorReference : workbenchWindow.getActivePage().getEditorReferences()) {
      if (file.equals(getFile(editorReference))) {
        workbenchWindow.getActivePage().closeEditors(new IEditorReference[] {editorReference}, false);
      }
    }
  }

  private static boolean isEnabled(IEvaluationContext evaluationContext) {
    Object variable = evaluationContext.getVariable(ISources.ACTIVE_EDITOR_INPUT_NAME);
    boolean result = false;
    if (variable instanceof IEditorInput) {
      IEditorInput editorInput = (IEditorInput) variable;
      result = ResourceUtil.getFile(editorInput) != null || getFile(editorInput) != null;
    }
    return result;
  }

  private static File getFile(IEditorReference editorReference) {
    try {
      return getFile(editorReference.getEditorInput());
    } catch (PartInitException e) {
      return null;
    }
  }

  private static File getFile(IEditorInput editorInput) {
    File result = null;
    if (editorInput instanceof IPathEditorInput) {
      IPathEditorInput pathEditorInput = (IPathEditorInput) editorInput;
      result = pathEditorInput.getPath().toFile();
    } else if (editorInput instanceof IURIEditorInput) {
      IURIEditorInput uriEditorInput = (IURIEditorInput) editorInput;
      result = URIUtil.toFile(uriEditorInput.getURI());
    }
    return result;
  }

  private static class DefaultDeleteEditorFilePrompter implements DeleteEditorFilePrompter {
    private static final String TITLE = "Delete File";

    @Override
    public boolean confirmDelete(IShellProvider shellProvider, File file) {
      String text = "Are you sure you want to delete ''{0}'' from the file system?";
      String message = MessageFormat.format(text, file.getName());
      return MessageDialog.openConfirm(shellProvider.getShell(), TITLE, message);
    }

    @Override
    public void showError(IShellProvider shellProvider, String message) {
      MessageDialog.openError(shellProvider.getShell(), TITLE, message);
    }
  }

}
