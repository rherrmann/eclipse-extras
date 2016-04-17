package com.codeaffine.extras.launch.internal.dialog;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.statushandlers.StatusManager;

public class LaunchTerminator {

  private final ILaunchConfiguration launchConfig;

  public LaunchTerminator( ILaunchConfiguration launchConfig ) {
    this.launchConfig = requireNonNull( launchConfig );
  }

  public void terminateLaunches() {
    getLaunches().stream()
      .filter( launch -> launchConfig.contentsEqual( launch.getLaunchConfiguration() ) )
      .forEach( LaunchTerminator::terminateLaunch );
  }

  private static List<ILaunch> getLaunches() {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    return Arrays.asList( launchManager.getLaunches() );
  }

  private static void terminateLaunch( ILaunch launch ) {
    try {
      launch.terminate();
    } catch( DebugException debugException ) {
      StatusManager.getManager().handle( debugException.getStatus(), StatusManager.LOG );
    }
  }
}
