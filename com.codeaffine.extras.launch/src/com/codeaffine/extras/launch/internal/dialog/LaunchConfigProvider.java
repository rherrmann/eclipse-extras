package com.codeaffine.extras.launch.internal.dialog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;


@SuppressWarnings("restriction")
public class LaunchConfigProvider {

  private final ILaunchManager launchManager;

  public LaunchConfigProvider(ILaunchManager launchManager) {
    this.launchManager = launchManager;
  }

  public ILaunchConfiguration[] getLaunchConfigurations() throws CoreException {
    return LaunchConfigurationManager.filterConfigs(launchManager.getLaunchConfigurations());
  }

}
