package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.test.LaunchModeHelper.TEST_LAUNCH_MODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.ILaunchGroup;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.internal.dialog.LaunchModeComputer;
import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchModeComputerPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private ILaunchConfigurationWorkingCopy launchConfig;

  @Before
  public void setUp() throws CoreException {
    launchConfig = launchConfigRule.createPublicLaunchConfig();
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullLaunchConfig() {
    new LaunchModeComputer(null, getSupportedMode());
  }

  @Test
  public void testComputeLaunchModeWithSupportedMode() {
    ILaunchMode supportedMode = getSupportedMode();

    ILaunchMode launchMode = computeLaunchMode(supportedMode);

    assertThat(launchMode).isEqualTo(supportedMode);
  }

  @Test
  public void testComputeLaunchModeWithUnsupportedMode() {
    ILaunchMode unsupportedMode = getUnsupportedMode();

    ILaunchMode launchMode = computeLaunchMode(unsupportedMode);

    assertThat(launchMode).isEqualTo(getSupportedMode());
  }

  @Test
  public void testComputeLaunchModeWithNullLaunchMode() {
    ILaunchMode launchMode = computeLaunchMode(null);

    assertThat(launchMode).isEqualTo(getSupportedMode());
  }

  @Test
  public void testComputeLaunchModeWithDeletedLaunchConfig() throws CoreException {
    ILaunchMode supportedMode = getSupportedMode();
    ILaunchConfiguration deletedLaunchConfig = launchConfig.doSave();
    deletedLaunchConfig.delete();

    ILaunchMode launchMode = new LaunchModeComputer(deletedLaunchConfig, supportedMode).computeLaunchMode();

    assertThat(launchMode).isNull();
  }

  @Test
  public void testComputeLaunchGroupWithSupportedMode() {
    ILaunchMode supportedMode = getSupportedMode();

    ILaunchGroup launchMode = computeLaunchGroup(supportedMode);

    assertThat(launchMode.getMode()).isEqualTo(supportedMode.getIdentifier());
  }

  @Test
  public void testComputeLaunchGroupWithUnsupportedMode() {
    ILaunchMode unsupportedMode = getUnsupportedMode();

    ILaunchGroup launchMode = computeLaunchGroup(unsupportedMode);

    assertThat(launchMode.getMode()).isEqualTo(getSupportedMode().getIdentifier());
  }

  @Test
  public void testComputeLaunchGroupWithNullLaunchMode() {
    ILaunchGroup launchGroup = computeLaunchGroup(null);

    assertThat(launchGroup.getMode()).isEqualTo(getSupportedMode().getIdentifier());
  }

  @Test
  public void testComputeLaunchGroupWithDeletedLaunchConfig() throws CoreException {
    ILaunchMode supportedMode = getSupportedMode();
    ILaunchConfiguration deletedLaunchConfig = launchConfig.doSave();
    deletedLaunchConfig.delete();

    ILaunchGroup launchGroup = new LaunchModeComputer(deletedLaunchConfig, supportedMode).computeLaunchGroup();

    assertThat(launchGroup).isNull();
  }

  private ILaunchMode computeLaunchMode(ILaunchMode preferredLaunchMode) {
    return new LaunchModeComputer(launchConfig, preferredLaunchMode).computeLaunchMode();
  }

  private ILaunchGroup computeLaunchGroup(ILaunchMode preferredLaunchMode) {
    return new LaunchModeComputer(launchConfig, preferredLaunchMode).computeLaunchGroup();
  }

  private static ILaunchMode getSupportedMode() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchMode(DEBUG_MODE);
  }

  private static ILaunchMode getUnsupportedMode() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchMode(TEST_LAUNCH_MODE);
  }
}
