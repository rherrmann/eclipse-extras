package com.codeaffine.extras.launch.internal;

import static org.eclipse.debug.ui.DebugUITools.openLaunchConfigurationDialog;
import static org.eclipse.jface.window.Window.OK;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;


public class EditLaunchConfigAction extends Action {

  private final LaunchSelectionDialog launchSelectionDialog;
  private IStructuredSelection selection;

  public EditLaunchConfigAction( LaunchSelectionDialog launchSelectionDialog ) {
    this.launchSelectionDialog = launchSelectionDialog;
    this.selection = StructuredSelection.EMPTY;
    setEnabled( false );
  }

  public void setSelection( IStructuredSelection selection ) {
    this.selection = selection;
    setEnabled( selection.size() == 1 && selection.getFirstElement() instanceof ILaunchConfiguration );
  }

  public IStructuredSelection getSelection() {
    return selection;
  }

  @Override
  public void run() {
    if( isEnabled() ) {
      if( editLaunchConfig( getSelectedLaunchConfig() ) ) {
        launchSelectionDialog.close( Window.CANCEL );
      } else {
        launchSelectionDialog.refresh();
      }
    }
  }

  protected boolean editLaunchConfig( ILaunchConfiguration launchConfig ) {
    Shell shell = launchSelectionDialog.getShell();
    String launchGroupId = getLaunchGroupId( launchConfig );
    return openLaunchConfigurationDialog( shell, launchConfig, launchGroupId, null ) == OK;
  }

  private String getLaunchGroupId( ILaunchConfiguration launchConfig ) {
    ILaunchMode preferredLaunchMode = launchSelectionDialog.getLaunchMode();
    LaunchModeComputer launchModeComputer = new LaunchModeComputer( launchConfig, preferredLaunchMode );
    ILaunchGroup launchGroup = launchModeComputer.computeLaunchGroup();
    return launchGroup == null ? null : launchGroup.getIdentifier();
  }

  private ILaunchConfiguration getSelectedLaunchConfig() {
    return ( ILaunchConfiguration )selection.getFirstElement();
  }
}
