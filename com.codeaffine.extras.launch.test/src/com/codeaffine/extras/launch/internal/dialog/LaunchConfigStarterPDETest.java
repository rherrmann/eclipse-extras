package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.preference.PreferenceStore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchConfigStarterPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private DebugUIPreferences preferences;
  private ILaunchManager launchManager;
  private ILaunchConfiguration launchConfig;

  @Before
  public void setUp() throws CoreException {
    preferences = new DebugUIPreferences(new PreferenceStore());
    launchManager = DebugPlugin.getDefault().getLaunchManager();
    launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullLaunchMode() {
    new LaunchConfigStarter(preferences, null);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullPreferences() {
    new LaunchConfigStarter(null, launchManager.getLaunchMode(RUN_MODE));
  }

  @Test
  public void testStart() {
    startLaunchConfig(launchConfig);

    assertThat(launchManager.getLaunches()).hasSize(1);
    assertThat(launchManager.getLaunches()[0].getLaunchConfiguration()).isEqualTo(launchConfig);
  }

  @Test
  public void testStartWithDisabledTerminateAndRelaunch() {
    preferences.setTerminateBeforeRelaunch(false);
    startLaunchConfig(launchConfig);

    startLaunchConfig(launchConfig);

    assertThat(launchManager.getLaunches()).hasSize(2);
    assertThat(launchManager.getLaunches()[0].getLaunchConfiguration()).isEqualTo(launchConfig);
    assertThat(launchManager.getLaunches()[1].getLaunchConfiguration()).isEqualTo(launchConfig);
  }

  @Test
  public void testStartWithEnabledTerminateAndRelaunch() {
    preferences.setTerminateBeforeRelaunch(true);
    startLaunchConfig(launchConfig);

    startLaunchConfig(launchConfig);

    assertThat(launchManager.getLaunches()).hasSize(1);
    assertThat(launchManager.getLaunches()[0].getLaunchConfiguration()).isEqualTo(launchConfig);
  }

  private void startLaunchConfig(ILaunchConfiguration launchConfig) {
    new LaunchConfigStarter(preferences, launchManager.getLaunchMode(RUN_MODE), launchConfig).start();
  }
}
