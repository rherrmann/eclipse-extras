package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchTerminatorPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  @Test(expected=NullPointerException.class)
  public void testConstructorWithNullArgument() {
    new LaunchTerminator( null );
  }

  @Test
  public void testTerminateLaunches() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();
    ILaunch launch = launchConfig.launch( RUN_MODE, new NullProgressMonitor() );

    new LaunchTerminator( launchConfig ).terminateLaunches();

    assertThat( launch.isTerminated() ).isTrue();
  }

  @Test
  public void testTerminateLaunchesWithMultipleLaunches() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();
    ILaunch launch1 = launchConfig.launch( RUN_MODE, new NullProgressMonitor() );
    ILaunch launch2 = launchConfig.launch( DEBUG_MODE, new NullProgressMonitor() );

    new LaunchTerminator( launchConfig ).terminateLaunches();

    assertThat( launch1.isTerminated() ).isTrue();
    assertThat( launch2.isTerminated() ).isTrue();
  }

  @Test
  public void testTerminateLaunchesWithTerminatedLaunch() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();
    ILaunch launch = launchConfig.launch( RUN_MODE, new NullProgressMonitor() );
    launch.terminate();

    new LaunchTerminator( launchConfig ).terminateLaunches();

    assertThat( launch.isTerminated() ).isTrue();
  }

  @Test
  public void testTerminateLaunchesWithUnrelatedLaunch() throws CoreException {
    ILaunchConfiguration unrelatedLaunchConfig = launchConfigRule.createPrivateLaunchConfig().doSave();
    ILaunch unrelatedLaunch = unrelatedLaunchConfig.launch( RUN_MODE, new NullProgressMonitor() );
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();

    new LaunchTerminator( launchConfig ).terminateLaunches();

    assertThat( unrelatedLaunch.isTerminated() ).isFalse();
  }

  @Test
  public void testTerminateLaunchesWithDeletedLaunchConfig() throws CoreException {
    ILaunchConfiguration unrelatedLaunchConfig = launchConfigRule.createPrivateLaunchConfig().doSave();
    ILaunch unrelatedLaunch = unrelatedLaunchConfig.launch( RUN_MODE, new NullProgressMonitor() );
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();
    unrelatedLaunchConfig.delete();

    new LaunchTerminator( launchConfig ).terminateLaunches();

    assertThat( unrelatedLaunch.isTerminated() ).isFalse();
  }

  @Test
  public void testTerminateLaunchesWithRenamedLaunchConfig() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();
    ILaunch launch = launchConfig.launch( RUN_MODE, new NullProgressMonitor() );
    launchConfigRule.renameLaunchConfig( launchConfig );

    new LaunchTerminator( launchConfig ).terminateLaunches();

    assertThat( launch.isTerminated() ).isFalse();
  }
}
