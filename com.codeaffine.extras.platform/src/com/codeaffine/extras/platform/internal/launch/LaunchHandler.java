package com.codeaffine.extras.platform.internal.launch;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;


public class LaunchHandler extends AbstractHandler {

  @Override
  public Object execute( ExecutionEvent event ) throws ExecutionException {
    Shell shell = HandlerUtil.getActiveWorkbenchWindow( event ).getShell();
    LaunchSelectionDialog dialog = new LaunchSelectionDialog( shell );
    if( dialog.open() == Window.OK ) {
      Object[] selectedElements = dialog.getResult();
      for( Object selectedElement : selectedElements ) {
        ILaunchConfiguration launchConfig = ( ILaunchConfiguration )selectedElement;
        DebugUITools.launch( launchConfig, dialog.getLaunchModeId() );
      }
    }
    return null;
  }
}
