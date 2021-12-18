package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.internal.LaunchExtrasPlugin.PLUGIN_ID;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.eclipse.core.runtime.IStatus.ERROR;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.statushandlers.StatusManager;

public class LaunchConfigStarter {

  private final ILaunchMode preferredLaunchMode;
  private final ILaunchConfiguration[] launchConfigs;
  private final DebugUIPreferences preferences;

  public LaunchConfigStarter(ILaunchMode launchMode, ILaunchConfiguration... launchConfigs) {
    this(new DebugUIPreferences(), launchMode, launchConfigs);
  }

  public LaunchConfigStarter(DebugUIPreferences preferences, ILaunchMode launchMode,
      ILaunchConfiguration... launchConfigs) {
    this.preferences = requireNonNull(preferences);
    this.preferredLaunchMode = requireNonNull(launchMode);
    this.launchConfigs = launchConfigs;
  }

  public void start() {
    terminateLaunches();
    startLaunchConfigs();
  }

  private void terminateLaunches() {
    if (preferences.isTerminateBeforeRelaunch()) {
      IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
      try {
        progressService.busyCursorWhile(this::terminateLaunches);
      } catch (InvocationTargetException ite) {
        handleException(ite.getCause());
      } catch (InterruptedException ignore) {
        Thread.interrupted();
      }
    }
  }

  private void startLaunchConfigs() {
    stream(launchConfigs).forEach(this::startLaunchConfig);
  }

  private void startLaunchConfig(ILaunchConfiguration launchConfig) {
    ILaunchMode launchMode = new LaunchModeComputer(launchConfig, preferredLaunchMode).computeLaunchMode();
    DebugUITools.launch(launchConfig, launchMode.getIdentifier());
  }

  private void terminateLaunches(IProgressMonitor monitor) {
    monitor.beginTask("Terminate previous launches...", launchConfigs.length);
    for (ILaunchConfiguration launchConfig : launchConfigs) {
      new LaunchTerminator(launchConfig).terminateLaunches();
      monitor.worked(1);
    }
    monitor.done();
  }

  private void handleException(Throwable exception) {
    IStatus status;
    if (exception instanceof CoreException) {
      status = ((CoreException) exception).getStatus();
    } else {
      String message;
      if (launchConfigs.length == 1) {
        message = "Failed to start launch configuration: " + getLaunchConfigNames();
      } else {
        message = "Failed to start launch configurations: " + getLaunchConfigNames();
      }
      status = new Status(ERROR, PLUGIN_ID, message, exception);
    }
    StatusManager.getManager().handle(status, StatusManager.LOG);
  }

  private String getLaunchConfigNames() {
    return stream(launchConfigs).map(ILaunchConfiguration::getName).collect(joining(", "));
  }

}
