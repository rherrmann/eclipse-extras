package com.codeaffine.extras.jdt.internal.junitstatus;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;


public class LaunchesAdapter implements ILaunchesListener2 {

  @Override
  public void launchesRemoved(ILaunch[] launches) {}

  @Override
  public void launchesAdded(ILaunch[] launches) {}

  @Override
  public void launchesChanged(ILaunch[] launches) {}

  @Override
  public void launchesTerminated(ILaunch[] launches) {}
}
