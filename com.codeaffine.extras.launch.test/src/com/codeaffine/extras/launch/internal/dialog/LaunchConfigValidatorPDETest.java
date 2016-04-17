package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.test.LaunchModeHelper.TEST_LAUNCH_MODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchMode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchConfigValidatorPDETest {
  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private ILaunchConfigurationWorkingCopy launchConfig;

  @Before
  public void setUp() throws CoreException {
    launchConfig = launchConfigRule.createPublicLaunchConfig();
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullLaunchConfig() {
    new LaunchConfigValidator( null, getSupportedMode() );
  }

  @Test
  public void testValidate() throws CoreException {
    launchConfig.doSave();

    IStatus status = new LaunchConfigValidator( launchConfig, getSupportedMode() ).validate();

    assertThat( status.isOK() ).isTrue();
  }

  @Test
  public void testValidateWithTransientLaunchConfig() {
    IStatus status = new LaunchConfigValidator( launchConfig, getSupportedMode() ).validate();

    assertThat( status.matches( IStatus.ERROR ) ).isTrue();
    assertThat( status.getMessage() ).isEqualTo( LaunchConfigValidator.LAUNCH_CONFIG_NOT_FOUND );
  }

  @Test
  public void testValidateWithUnsupportedLaunchMode() throws CoreException {
    launchConfig.doSave();

    IStatus status = new LaunchConfigValidator( launchConfig, getUnsupportedMode() ).validate();

    assertThat( status.isOK() ).isFalse();
  }

  @Test
  public void testValidateWithDeletedLaunchConfig() throws CoreException {
    ILaunchConfiguration deletedLaunchConfig = launchConfig.doSave();
    deletedLaunchConfig.delete();

    IStatus status = new LaunchConfigValidator( deletedLaunchConfig, getSupportedMode() ).validate();

    assertThat( status.matches( IStatus.ERROR ) ).isTrue();
    assertThat( status.getMessage() ).isEqualTo( LaunchConfigValidator.LAUNCH_CONFIG_NOT_FOUND );
  }

  private static ILaunchMode getSupportedMode() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchMode( DEBUG_MODE );
  }

  private static ILaunchMode getUnsupportedMode() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchMode( TEST_LAUNCH_MODE );
  }
}
