package com.codeaffine.extras.launch.test;

import static org.eclipse.debug.ui.IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
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

  public LaunchConfigRule() {
    launchManager = DebugPlugin.getDefault().getLaunchManager();
  }

  @Override
  protected void before() throws CoreException {
    // workaround for bug 482711 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=482711)
    ILaunchConfiguration launchConfig = newPublicLaunchConfig().doSave();
    launchConfig.delete();
  }

  @Override
  protected void after() {
    try {
      terminateLaunches();
      deleteLaunchConfigs();
    } catch( CoreException ce ) {
      throw new RuntimeException( ce );
    }
  }

  public ILaunchConfigurationWorkingCopy createPublicLaunchConfig() throws CoreException {
    ILaunchConfigurationWorkingCopy result = newPublicLaunchConfig();
    result.setAttribute( ATTR_LAUNCH_IN_BACKGROUND, false );
    return result;
  }

  public ILaunchConfigurationWorkingCopy createPrivateLaunchConfig() throws CoreException {
    ILaunchConfigurationWorkingCopy result = newPrivateLaunchConfig();
    result.setAttribute( ATTR_LAUNCH_IN_BACKGROUND, false );
    return result;
  }

  public ILaunchConfigurationType getPublicTestLaunchConfigType() {
    return launchManager.getLaunchConfigurationType( PUBLIC_TEST_LAUNCH_CONFIG_TYPE );
  }

  public ILaunchConfigurationType getPrivateTestLaunchConfigType() {
    return launchManager.getLaunchConfigurationType( PRIVATE_TEST_LAUNCH_CONFIG_TYPE );
  }

  public String renameLaunchConfig( ILaunchConfiguration launchConfig ) throws CoreException {
    String newName = launchConfig.getName() + "-renamed";
    if( launchManager.isExistingLaunchConfigurationName( newName ) ) {
      newName = launchManager.generateLaunchConfigurationName( newName );
    }
    ILaunchConfigurationWorkingCopy workingCopy = launchConfig.getWorkingCopy();
    workingCopy.rename( newName );
    workingCopy.doSave();
    return newName;
  }

  private ILaunchConfigurationWorkingCopy newPublicLaunchConfig() throws CoreException {
    ILaunchConfigurationType type = getPublicTestLaunchConfigType();
    return type.newInstance( null, getUniqueLaunchConfigName() );
  }

  private ILaunchConfigurationWorkingCopy newPrivateLaunchConfig() throws CoreException {
    ILaunchConfigurationType type = getPrivateTestLaunchConfigType();
    return type.newInstance( null, getUniqueLaunchConfigName() );
  }

  private String getUniqueLaunchConfigName() {
    return launchManager.generateLaunchConfigurationName( "LC" );
  }

  private void terminateLaunches() throws DebugException {
    for( ILaunch launch : launchManager.getLaunches() ) {
      launch.terminate();
    }
  }

  private void deleteLaunchConfigs() throws CoreException {
    for( ILaunchConfiguration launchConfiguration : launchManager.getLaunchConfigurations() ) {
      launchConfiguration.delete();
    }
  }

}
