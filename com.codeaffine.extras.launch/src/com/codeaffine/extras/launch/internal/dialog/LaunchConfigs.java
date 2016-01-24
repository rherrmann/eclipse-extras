package com.codeaffine.extras.launch.internal.dialog;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

public class LaunchConfigs {

  public static boolean isRunning( ILaunchConfiguration launchConfig ) {
    requireNonNull( launchConfig, "launchConfig" );
    ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
    return Stream.of( launches )
      .anyMatch( launch -> !launch.isTerminated() && launchConfig.equals( launch.getLaunchConfiguration() ) );
  }

  private LaunchConfigs() { }
}
