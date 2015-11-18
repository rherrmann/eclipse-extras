package com.codeaffine.extras.launch.test;

import static org.eclipse.debug.ui.IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.junit.rules.ExternalResource;


public class LaunchConfigurationRule extends ExternalResource {

  private static final String TEST_LAUNCH_CONFIG_TYPE
    = "com.codeaffine.extras.launch.test.TestLaunchConfigurationType";

  private static int counter;

  private final ILaunchManager launchManager;
  private final List<String> launchConfiguarionNames;

  public LaunchConfigurationRule() {
    launchManager = DebugPlugin.getDefault().getLaunchManager();
    launchConfiguarionNames = new ArrayList<>();
  }

  @Override
  protected void after() {
    launchConfiguarionNames.forEach( this::deleteLaunchConfig );
  }

  public ILaunchConfigurationWorkingCopy createLaunchConfig() throws CoreException {
    ILaunchConfigurationType type = getTestLaunchConfigType();
    ILaunchConfigurationWorkingCopy result = type.newInstance( null, getUniqueLaunchConfigName() );
    result.setAttribute( ATTR_LAUNCH_IN_BACKGROUND, false );
    launchConfiguarionNames.add( result.getName() );
    return result;
  }

  public void deleteLaunchConfig( String name ) {
    try {
      ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations();
      for( ILaunchConfiguration launchConfig : launchConfigurations ) {
        if( name.equals( launchConfig.getName() ) ) {
          launchConfig.delete();
        }
      }
    } catch( CoreException ce ) {
      throw new RuntimeException( ce );
    }
  }

  private static ILaunchConfigurationType getTestLaunchConfigType() {
    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    return manager.getLaunchConfigurationType( TEST_LAUNCH_CONFIG_TYPE );
  }

  private static String getUniqueLaunchConfigName() {
    String result = "LC-" + counter;
    counter++;
    return result;
  }
}
