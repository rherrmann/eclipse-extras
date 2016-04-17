package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchConfigsPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  @Test(expected = NullPointerException.class)
  public void testIsRunningWithNullArgument() {
    LaunchConfigs.isRunning( null );
  }

  @Test
  public void testIsRunning() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createPublicLaunchConfig();

    boolean running = LaunchConfigs.isRunning( launchConfig );

    assertThat( running ).isFalse();
  }

  @Test
  public void testIsRunningWithLaunchedLaunchConfig() throws CoreException {
    ILaunchConfigurationWorkingCopy launchConfig = launchConfigRule.createPublicLaunchConfig();
    ILaunch launch = launchConfig.launch( RUN_MODE, null );

    boolean running = LaunchConfigs.isRunning( launchConfig );
    launch.terminate();

    assertThat( running ).isTrue();
  }

}
