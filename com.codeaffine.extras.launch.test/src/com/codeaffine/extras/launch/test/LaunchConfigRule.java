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


public class LaunchConfigRule extends ExternalResource {

  private static final String PUBLIC_TEST_LAUNCH_CONFIG_TYPE
    = "com.codeaffine.extras.launch.test.PublicTestLaunchConfigurationType";
  private static final String PRIVATE_TEST_LAUNCH_CONFIG_TYPE
    = "com.codeaffine.extras.launch.test.PrivateTestLaunchConfigurationType";

  private final ILaunchManager launchManager;
  private final List<String> launchConfiguarionNames;

  public LaunchConfigRule() {
    launchManager = DebugPlugin.getDefault().getLaunchManager();
    launchConfiguarionNames = new ArrayList<>();
  }

  @Override
  protected void after() {
    launchConfiguarionNames.forEach( this::deleteLaunchConfig );
  }

  public ILaunchConfigurationWorkingCopy createLaunchConfig() throws CoreException {
    ILaunchConfigurationType type = getPublicTestLaunchConfigType();
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

  public ILaunchConfigurationType getPublicTestLaunchConfigType() {
    return launchManager.getLaunchConfigurationType( PUBLIC_TEST_LAUNCH_CONFIG_TYPE );
  }

  public ILaunchConfigurationType getPrivateTestLaunchConfigType() {
    return launchManager.getLaunchConfigurationType( PRIVATE_TEST_LAUNCH_CONFIG_TYPE );
  }

  private String getUniqueLaunchConfigName() {
    return launchManager.generateLaunchConfigurationName( "LC" );
  }
}
