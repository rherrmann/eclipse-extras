package com.codeaffine.extras.platform.test;

import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;


public class LaunchManagerHelper {

  private static final String TEST_LAUNCH_CONFIG_TYPE
    = "com.codeaffine.extras.platform.test.TestLaunchConfigurationType";

  public static ILaunchConfigurationWorkingCopy createLaunchConfig() throws CoreException {
    ILaunchConfigurationType type = getTestLaunchConfigType();
    return type.newInstance( null, "LC" + new Object().hashCode() );
  }

  public static String getDebugModeLabel() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchMode( DEBUG_MODE ).getLabel();
  }

  private static ILaunchConfigurationType getTestLaunchConfigType() {
    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    return manager.getLaunchConfigurationType( TEST_LAUNCH_CONFIG_TYPE );
  }
}
