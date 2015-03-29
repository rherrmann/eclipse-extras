package com.codeaffine.extras.ide.internal.launch;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;


public class LaunchHandler extends AbstractHandler {

  public static final String COMMAND_ID = "com.codeaffine.extras.ide.internal.LaunchCommand";

  @Override
  public Object execute( ExecutionEvent event ) throws ExecutionException {
    LaunchSelectionDialog dialog = createDialog( event );
    if( dialog.open() == Window.OK ) {
      launchSelectedElements( dialog.getLaunchModeId(), dialog.getResult() );
    }
    return null;
  }

  private static LaunchSelectionDialog createDialog( ExecutionEvent event ) {
    Shell shell = HandlerUtil.getActiveWorkbenchWindow( event ).getShell();
    return new LaunchSelectionDialog( shell );
  }

  private static void launchSelectedElements( String launchModeId, Object[] selectedElements ) {
    for( Object selectedElement : selectedElements ) {
      ILaunchConfiguration launchConfig = ( ILaunchConfiguration )selectedElement;
      DebugUITools.launch( launchConfig, launchModeId );
    }
  }
}
