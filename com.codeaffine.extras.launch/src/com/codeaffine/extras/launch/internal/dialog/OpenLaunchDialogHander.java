package com.codeaffine.extras.launch.internal.dialog;

import static org.eclipse.jface.window.Window.OK;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;


public class OpenLaunchDialogHander extends AbstractHandler {

  public static final String COMMAND_ID = "com.codeaffine.extras.launch.internal.OpenLaunchDialogCommand";

  @Override
  public Object execute(ExecutionEvent event) {
    LaunchSelectionDialog dialog = createDialog(event);
    if (dialog.open() == OK) {
      startLaunchConfigs(dialog.getLaunchMode(), dialog.getSelectedLaunchConfigurations());
    }
    return null;
  }

  private static void startLaunchConfigs(ILaunchMode launchMode, ILaunchConfiguration[] launchConfigs) {
    new LaunchConfigStarter(launchMode, launchConfigs).start();
  }

  protected LaunchSelectionDialog createDialog(ExecutionEvent event) {
    Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
    return new LaunchSelectionDialog(shell);
  }

}
