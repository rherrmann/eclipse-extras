package com.codeaffine.extras.archive.internal.extract;

import static java.util.Arrays.asList;
import java.util.Collection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.NewFolderDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import com.codeaffine.extras.archive.internal.util.StatusUtil;

class WorkspaceFolderSelectionDialog extends ElementTreeSelectionDialog {

  private final IWorkspaceRoot workspaceRoot;

  WorkspaceFolderSelectionDialog(Shell shell) {
    super(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
    workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    initialize();
  }

  private void initialize() {
    setAllowMultiple(false);
    setStatusLineAboveButtons(false);
    setValidator(new WorkspaceFolderValidator());
    addFilter(new WorkspaceFolderFilter());
    setComparator(new ResourceComparator(ResourceComparator.NAME));
    setTitle("Folder Selection");
    setMessage("Choose a folder to which the selected items will be extracted");
    setInput(workspaceRoot);
    setHelpAvailable(false);
  }

  void setInitialSelection(IPath path) {
    setInitialSelection(workspaceRoot.findMember(path));
  }

  IPath getSelection() {
    IContainer selectedFolder = (IContainer) getFirstResult();
    return selectedFolder.getFullPath();
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    Button button = createButton(parent, IDialogConstants.OPEN_ID, "&New Folder...", false);
    button.addListener(SWT.Selection, event -> handleNewFolder());
    super.createButtonsForButtonBar(parent);
  }

  @Override
  protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
    TreeViewer result = super.doCreateTreeViewer(parent, style);
    result.addSelectionChangedListener(this::handleSelectionChanged);
    return result;
  }

  private void handleSelectionChanged(SelectionChangedEvent event) {
    Button button = getButton(IDialogConstants.OPEN_ID);
    if (button != null) {
      button.setEnabled(event.getStructuredSelection().getFirstElement() instanceof IContainer);
    }
  }

  private void handleNewFolder() {
    if (getFirstResult() instanceof IContainer) {
      NewFolderDialog dialog = new NewFolderDialog(getShell(), (IContainer) getFirstResult());
      if (dialog.open() == Window.OK) {
        selectElement(dialog.getFirstResult());
      }
    }
  }

  private void selectElement(Object element) {
    ISelection selection = new StructuredSelection(element);
    getTreeViewer().setSelection(selection, true);
  }

  static class WorkspaceFolderFilter extends ViewerFilter {
    private static final Class<?>[] ACCEPTED_TYPES = new Class[] {IProject.class, IFolder.class};

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
      boolean result = false;
      for (int i = 0; !result && i < ACCEPTED_TYPES.length; i++) {
        if (ACCEPTED_TYPES[i].isInstance(element)) {
          result = true;
        }
      }
      return result;
    }
  }

  static class WorkspaceFolderValidator implements ISelectionStatusValidator {
    private static final Collection<Class<?>> ACCEPTED_TYPES = asList(IProject.class, IFolder.class);

    @Override
    public IStatus validate(Object[] elements) {
      IStatus result;
      if (!isValid(elements)) {
        result = StatusUtil.createStatus(IStatus.ERROR, "", null);
      } else {
        result = StatusUtil.createStatus(IStatus.OK, "", null);
      }
      return result;
    }

    private static boolean isValid(Object[] selection) {
      return selection.length == 1 && isOfAcceptedType(selection[0]);
    }

    private static boolean isOfAcceptedType(Object object) {
      return ACCEPTED_TYPES.stream().anyMatch(type -> type.isInstance(object));
    }
  }

}
