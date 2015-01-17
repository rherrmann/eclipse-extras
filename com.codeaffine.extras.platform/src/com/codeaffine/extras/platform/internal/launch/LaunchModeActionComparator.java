package com.codeaffine.extras.platform.internal.launch;

import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

import java.util.Comparator;

import org.eclipse.debug.core.ILaunchMode;


public class LaunchModeActionComparator implements Comparator<LaunchModeAction> {

  @Override
  public int compare( LaunchModeAction action1, LaunchModeAction action2 ) {
    return compare( action1.getLaunchMode(), action2.getLaunchMode() );
  }

  private static int compare( ILaunchMode launchMode1, ILaunchMode launchMode2 ) {
    int result;
    if( isRunMode( launchMode1 ) ) {
      result = -1;
    } else if( isRunMode( launchMode2 ) ) {
      result = 1;
    } else if( isDebugMode( launchMode1 ) ) {
      if( isRunMode( launchMode2 ) ) {
        result = 1;
      } else {
        result = -1;
      }
    } else if( isDebugMode( launchMode2 ) ) {
      if( isRunMode( launchMode1 ) ) {
        result = -1;
      } else {
        result = 1;
      }
    } else {
      result = launchMode1.getLabel().compareTo( launchMode2.getLabel() );
    }
    return result;
  }

  private static boolean isRunMode( ILaunchMode launchMode ) {
    return RUN_MODE.equals( launchMode.getIdentifier() );
  }

  private static boolean isDebugMode( ILaunchMode launchMode ) {
    return DEBUG_MODE.equals( launchMode.getIdentifier() );
  }
}
