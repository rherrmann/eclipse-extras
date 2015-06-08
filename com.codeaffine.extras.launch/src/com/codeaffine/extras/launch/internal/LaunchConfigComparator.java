package com.codeaffine.extras.launch.internal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfiguration;

public class LaunchConfigComparator implements Comparator<ILaunchConfiguration> {
  private final List<Object> launchConfigHistory;

  public LaunchConfigComparator( LaunchConfigSelectionHistory launchConfigHistory ) {
    this.launchConfigHistory = Arrays.asList( launchConfigHistory.getHistoryItems() );
  }

  @Override
  public int compare( ILaunchConfiguration launchConfig1, ILaunchConfiguration launchConfig2 ) {
    int result;
    if( isHistoryElement( launchConfig1 ) && isHistoryElement( launchConfig2 ) ) {
      result = launchConfigHistory.indexOf( launchConfig1 ) - launchConfigHistory.indexOf( launchConfig2 );
    } else {
      result = launchConfig1.getName().compareTo( launchConfig2.getName() );
    }
    return result;
  }

  private boolean isHistoryElement( ILaunchConfiguration launchConfig ) {
    return launchConfigHistory.contains( launchConfig );
  }
}