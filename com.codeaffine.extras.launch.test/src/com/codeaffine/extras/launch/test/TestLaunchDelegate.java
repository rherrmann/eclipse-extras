package com.codeaffine.extras.launch.test;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;


public class TestLaunchDelegate implements ILaunchConfigurationDelegate2 {

  @Override
  public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) {}

  @Override
  public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
    return new TestLaunch(configuration, mode, null);
  }

  @Override
  public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
      throws CoreException {
    return false;
  }

  @Override
  public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
      throws CoreException {
    return true;
  }

  @Override
  public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
      throws CoreException {
    return true;
  }
}
