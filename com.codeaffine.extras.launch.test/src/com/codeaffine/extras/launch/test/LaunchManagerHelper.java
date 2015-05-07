package com.codeaffine.extras.launch.test;

import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;


public class LaunchManagerHelper {

  private static final String TEST_LAUNCH_CONFIG_TYPE
    = "com.codeaffine.extras.ide.test.TestLaunchConfigurationType";

  public static ILaunchConfigurationWorkingCopy createLaunchConfig() throws CoreException {
    ILaunchConfigurationType type = getTestLaunchConfigType();
    return type.newInstance( null, "LC" + new Object().hashCode() );
  }

  public static void deleteLaunchConfig( String name ) throws CoreException {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations();
    for( ILaunchConfiguration launchConfig : launchConfigurations ) {
      if( name.equals( launchConfig.getName() ) ) {
        launchConfig.delete();
      }
    }
  }

  public static String getDebugModeLabel() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchMode( DEBUG_MODE ).getLabel();
  }

  private static ILaunchConfigurationType getTestLaunchConfigType() {
    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    return manager.getLaunchConfigurationType( TEST_LAUNCH_CONFIG_TYPE );
  }
}
