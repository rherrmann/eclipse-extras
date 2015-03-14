package com.codeaffine.extras.ide.internal.workingset;

import static com.codeaffine.extras.ide.internal.Images.WORKING_SET_WIZBAN;
import static com.codeaffine.extras.ide.internal.Images.getImageDescriptor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;

import com.codeaffine.extras.ide.internal.IDEExtrasPlugin;

public class DynamicWorkingSetWizard extends Wizard implements INewWizard {

  public static final String ID = "com.codeaffine.extras.ide.internal.DynamicWorkingSetWizard";

  private IWorkbench workbench;
  private DynamicWorkingSetPage page;

  public DynamicWorkingSetWizard() {
    setDefaultPageImageDescriptor( getImageDescriptor( WORKING_SET_WIZBAN ) );
    setDialogSettings( IDEExtrasPlugin.getInstance().getDialogSettings() );
    setWindowTitle( "New Dynamic Project Working Set" );
  }

  @Override
  public void init( IWorkbench workbench, IStructuredSelection selection ) {
    this.workbench = workbench;
  }

  @Override
  public void addPages() {
    page = new DynamicWorkingSetPage();
    page.setSelection( createWorkingSet() );
    addPage( page );
  }

  @Override
  public boolean performFinish() {
    page.finish();
    workbench.getWorkingSetManager().addWorkingSet( page.getSelection() );
    return true;
  }

  private IWorkingSet createWorkingSet() {
    IWorkingSet result = workbench.getWorkingSetManager().createWorkingSet( "", new IAdaptable[ 0 ] );
    result.setId( DynamicWorkingSet.ID );
    return result;
  }
}
