package com.codeaffine.extras.launch.test;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ISourceLocator;


public class TestLaunch extends Launch {

  private volatile boolean terminated;

  public TestLaunch( ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator ) {
    super( launchConfiguration, mode, locator );
  }

  @Override
  public boolean canTerminate() {
    return !terminated;
  }

  @Override
  public boolean isTerminated() {
    return terminated;
  }

  @Override
  public void terminate() throws DebugException {
    terminated = true;
    super.terminate();
    fireTerminate();
  }
}
