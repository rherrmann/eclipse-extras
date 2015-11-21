package com.codeaffine.extras.launch.test;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ISourceLocator;


public class TestLaunch extends Launch {

  public TestLaunch( ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator ) {
    super( launchConfiguration, mode, locator );
  }

  @Override
  public void terminate() throws DebugException {
    super.terminate();
    fireTerminate();
  }
}
