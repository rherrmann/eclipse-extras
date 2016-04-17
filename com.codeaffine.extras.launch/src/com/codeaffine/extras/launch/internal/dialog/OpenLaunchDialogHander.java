package com.codeaffine.extras.launch.internal.dialog;

import static org.eclipse.jface.window.Window.OK;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;


public class OpenLaunchDialogHander extends AbstractHandler {

  public static final String COMMAND_ID = "com.codeaffine.extras.launch.internal.OpenLaunchDialogCommand";

  @Override
  public Object execute( ExecutionEvent event ) {
    LaunchSelectionDialog dialog = createDialog( event );
    if( dialog.open() == OK ) {
      new LaunchConfigStarter( dialog.getLaunchMode(), dialog.getSelectedLaunchConfigurations() ).start();;
    }
    return null;
  }

  protected LaunchSelectionDialog createDialog( ExecutionEvent event ) {
    Shell shell = HandlerUtil.getActiveWorkbenchWindow( event ).getShell();
    return new LaunchSelectionDialog( shell );
  }

}
