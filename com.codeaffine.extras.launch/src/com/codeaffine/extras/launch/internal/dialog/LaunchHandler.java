package com.codeaffine.extras.launch.internal.dialog;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;


public class LaunchHandler extends AbstractHandler {

  public static final String COMMAND_ID = "com.codeaffine.extras.launch.internal.LaunchCommand";

  @Override
  public Object execute( ExecutionEvent event ) {
    LaunchSelectionDialog dialog = createDialog( event );
    if( dialog.open() == Window.OK ) {
      launch( dialog.getLaunchMode(), dialog.getSelectedLaunchConfigurations() );
    }
    return null;
  }

  private static LaunchSelectionDialog createDialog( ExecutionEvent event ) {
    Shell shell = HandlerUtil.getActiveWorkbenchWindow( event ).getShell();
    return new LaunchSelectionDialog( shell );
  }

  private static void launch( ILaunchMode preferredLaunchMode, ILaunchConfiguration[] launchConfigs ) {
    for( ILaunchConfiguration launchConfig : launchConfigs ) {
      ILaunchMode launchMode = new LaunchModeComputer( launchConfig, preferredLaunchMode ).computeLaunchMode();
      DebugUITools.launch( launchConfig, launchMode.getIdentifier() );
    }
  }
}
