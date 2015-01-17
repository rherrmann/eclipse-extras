package com.codeaffine.extras.platform.internal.launch;

import java.util.Comparator;

import org.eclipse.debug.core.ILaunchConfiguration;

public class LaunchConfigComparator implements Comparator<ILaunchConfiguration> {

  @Override
  public int compare( ILaunchConfiguration configuration1, ILaunchConfiguration configuration2 ) {
    return configuration1.getName().compareTo( configuration2.getName() );
  }
}