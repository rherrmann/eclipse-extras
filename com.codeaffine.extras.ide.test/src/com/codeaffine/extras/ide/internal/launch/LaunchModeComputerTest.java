package com.codeaffine.extras.ide.internal.launch;

import static com.codeaffine.extras.ide.test.LaunchModeHelper.TEST_LAUNCH_MODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.ide.test.LaunchManagerHelper;

public class LaunchModeComputerTest {

  private ILaunchConfigurationWorkingCopy launchConfig;

  @Before
  public void setUp() throws CoreException {
    launchConfig = LaunchManagerHelper.createLaunchConfig();
  }

  @After
  public void tearDown() throws CoreException {
    LaunchManagerHelper.deleteLaunchConfig( launchConfig.getName() );
  }

  @Test
  public void testComputeWithSupportedMode() {
    ILaunchMode supportedMode = getSupportedMode();

    ILaunchMode launchMode = new LaunchModeComputer( launchConfig, supportedMode ).compute();

    assertThat( launchMode ).isEqualTo( supportedMode );
  }

  @Test
  public void testComputeWithUnsupportedMode() {
    ILaunchMode unsupportedMode = getUnsupportedMode();

    ILaunchMode launchMode = new LaunchModeComputer( launchConfig, unsupportedMode ).compute();

    assertThat( launchMode ).isEqualTo( getSupportedMode() );
  }

  @Test
  public void testComputeWithNullLaunchMode() {
    ILaunchMode launchMode = new LaunchModeComputer( launchConfig, null ).compute();

    assertThat( launchMode ).isEqualTo( getSupportedMode() );
  }

  private static ILaunchMode getSupportedMode() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchMode( DEBUG_MODE );
  }

  private static ILaunchMode getUnsupportedMode() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchMode( TEST_LAUNCH_MODE );
  }
}
