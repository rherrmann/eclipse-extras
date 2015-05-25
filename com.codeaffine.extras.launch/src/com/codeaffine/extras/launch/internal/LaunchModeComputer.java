package com.codeaffine.extras.launch.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;


public class LaunchModeComputer {

  private final ILaunchConfiguration launchConfig;
  private final ILaunchMode preferredLaunchMode;

  public LaunchModeComputer( ILaunchConfiguration launchConfig, ILaunchMode preferredLaunchMode ) {
    this.launchConfig = launchConfig;
    this.preferredLaunchMode = preferredLaunchMode;
  }

  public ILaunchMode computeLaunchMode() {
    ILaunchMode result;
    if( isLaunchModeSupported( preferredLaunchMode ) ) {
      result = preferredLaunchMode;
    } else {
      result = findAlternativeLaunchMode();
    }
    return result;
  }

  public ILaunchGroup computeLaunchGroup() {
    ILaunchGroup result = null;
    ILaunchMode launchMode = computeLaunchMode();
    if( launchMode != null ) {
      result = DebugUITools.getLaunchGroup( launchConfig, launchMode.getIdentifier() );
    }
    return result;
  }

  private ILaunchMode findAlternativeLaunchMode() {
    ILaunchMode result = null;
    ILaunchMode[] launchModes = allLaunchModes();
    for( int i = 0; result == null && i < launchModes.length; i++ ) {
      if( isLaunchModeSupported( launchModes[ i ] ) ) {
        result = launchModes[ i ];
      }
    }
    return result;
  }

  private boolean isLaunchModeSupported( ILaunchMode launchMode ) {
    try {
      return launchMode != null && launchConfig.supportsMode( launchMode.getIdentifier() );
    } catch( CoreException ce ) {
      throw new RuntimeException( ce );
    }
  }

  private static ILaunchMode[] allLaunchModes() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchModes();
  }
}
