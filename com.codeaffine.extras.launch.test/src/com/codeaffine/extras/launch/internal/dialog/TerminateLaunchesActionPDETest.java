package com.codeaffine.extras.launch.internal.dialog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.junit.Assert.fail;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import com.codeaffine.extras.launch.internal.Images;
import com.codeaffine.extras.launch.internal.dialog.TerminateLaunchesAction.TerminateLaunchesJob;
import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class TerminateLaunchesActionPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private TerminateLaunchesJobListener terminateLaunchesJobListener;
  private TerminateLaunchesAction action;

  @Before
  public void setUp() {
    terminateLaunchesJobListener = new TerminateLaunchesJobListener();
    Job.getJobManager().addJobChangeListener(terminateLaunchesJobListener);
    action = new TerminateLaunchesAction();
  }

  @After
  public void tearDown() {
    Job.getJobManager().removeJobChangeListener(terminateLaunchesJobListener);
  }

  @Test
  public void testGetId() {
    assertThat(action.getId()).isEqualTo(TerminateLaunchesAction.ID);
  }

  @Test
  public void testGetText() {
    String text = action.getText();

    assertThat(text).isNotEmpty();
  }

  @Test
  public void testGetImageDescriptor() {
    ImageDescriptor imageDescriptor = action.getImageDescriptor();

    assertThat(imageDescriptor).isEqualTo(Images.getImageDescriptor(Images.TERMINATE_ALL));
  }

  @Test
  public void testInitialEnablement() {
    assertThat(action.isEnabled()).isFalse();
  }

  @Test
  public void testInitialSelection() {
    assertThat(action.getSelection().isEmpty()).isTrue();
  }

  @Test
  public void testSetSelectionWithArbitraryObject() {
    action.setSelection(new StructuredSelection(new Object()));

    assertThat(action.isEnabled()).isFalse();
  }

  @Test
  public void testSetSelectionToLaunchConfig() throws CoreException {
    action.setSelection(new StructuredSelection(launchConfigRule.createPublicLaunchConfig()));

    assertThat(action.isEnabled()).isFalse();
  }

  @Test
  public void testSetSelectionToRunningLaunchConfig() throws CoreException {
    ILaunchConfiguration launchConfig = createRunningLaunchConfig();

    action.setSelection(new StructuredSelection(launchConfig));

    assertThat(action.isEnabled()).isTrue();
  }

  @Test
  public void testSetSelectionToMultipleRunningLaunchConfigs() throws CoreException {
    ILaunchConfiguration launchConfig1 = createRunningLaunchConfig();
    ILaunchConfiguration launchConfig2 = createRunningLaunchConfig();

    action.setSelection(new StructuredSelection(new Object[] {launchConfig1, launchConfig2}));

    assertThat(action.isEnabled()).isTrue();
  }

  @Test
  public void testSetSelectionToMixedSelection() throws CoreException {
    ILaunchConfiguration launchConfig = createRunningLaunchConfig();

    action.setSelection(new StructuredSelection(new Object[] {launchConfig, new Object()}));

    assertThat(action.isEnabled()).isTrue();
  }

  @Test
  public void testRun() throws Exception {
    ILaunch launch = runLaunchConfig();
    action.setSelection(new StructuredSelection(launch.getLaunchConfiguration()));

    action.run();
    waitForTerminateLaunchesJob();

    assertThat(launch.isTerminated()).isTrue();
  }

  @Test
  public void testRunWithMultipleLaunches() throws Exception {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();
    ILaunch launch1 = launchConfig.launch(RUN_MODE, null);
    ILaunch launch2 = launchConfig.launch(RUN_MODE, null);
    action.setSelection(new StructuredSelection(launchConfig));

    action.run();
    waitForTerminateLaunchesJob();

    assertThat(launch1.isTerminated()).isTrue();
    assertThat(launch2.isTerminated()).isTrue();
  }

  @Test
  public void testRunWithMultipleLaunchConfigs() throws Exception {
    ILaunch launch1 = runLaunchConfig();
    ILaunch launch2 = runLaunchConfig();
    ILaunchConfiguration launchConfig1 = launch1.getLaunchConfiguration();
    ILaunchConfiguration launchConfig2 = launch2.getLaunchConfiguration();
    action.setSelection(new StructuredSelection(new Object[] {launchConfig1, launchConfig2}));

    action.run();
    waitForTerminateLaunchesJob();

    assertThat(launch1.isTerminated()).isTrue();
    assertThat(launch2.isTerminated()).isTrue();
  }

  private void waitForTerminateLaunchesJob() throws OperationCanceledException, InterruptedException {
    long start = System.nanoTime();
    while (!terminateLaunchesJobListener.isJobDone()) {
      if (Duration.ofNanos(System.nanoTime() - start).toSeconds() > 30) {
        fail("Timed out while waiting for TerminateLaunchesJob to finish");
      }
      Job.getJobManager().join(TerminateLaunchesJob.FAMILY, null);
    }
  }

  private ILaunchConfiguration createRunningLaunchConfig() throws CoreException {
    return runLaunchConfig().getLaunchConfiguration();
  }

  private ILaunch runLaunchConfig() throws CoreException {
    ILaunchConfiguration launchConfig = launchConfigRule.createPublicLaunchConfig().doSave();
    return launchConfig.launch(RUN_MODE, null);
  }

  private class TerminateLaunchesJobListener extends JobChangeAdapter {
    final AtomicBoolean jobDone;

    TerminateLaunchesJobListener() {
      jobDone = new AtomicBoolean();
    }

    boolean isJobDone() {
      return jobDone.get();
    }

    @Override
    public void done(IJobChangeEvent event) {
      if (event.getJob() instanceof TerminateLaunchesJob) {
        jobDone.set(true);
      }
    }
  }
}
