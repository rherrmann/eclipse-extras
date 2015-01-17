package com.codeaffine.extras.platform.test;

import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;


public class LaunchManagerHelper {

  public static ILaunchConfigurationWorkingCopy createLaunchConfig() throws CoreException {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    ILaunchConfigurationType launchConfigType = launchManager.getLaunchConfigurationTypes()[ 0 ];
    return launchConfigType.newInstance( null, "LC" + new Object().hashCode() );
  }

  public static String getDebugModeLabel() {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    return launchManager.getLaunchMode( DEBUG_MODE ).getLabel();
  }
}
