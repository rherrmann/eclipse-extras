package com.codeaffine.extras.platform.internal.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.IMemento;

import com.codeaffine.extras.platform.internal.launch.LaunchSelectionDialog.AccessibleSelectionHistory;
import com.google.common.base.Objects;

class LaunchConfigSelectionHistory extends AccessibleSelectionHistory {
  private static final String TAG_NAME = "name";

  private final ILaunchManager launchManager;

  LaunchConfigSelectionHistory( ILaunchManager launchManager ) {
    this.launchManager = launchManager;
  }

  @Override
  protected Object restoreItemFromMemento( IMemento memento ) {
    String launchConfigName = memento.getString( TAG_NAME );
    return findLaunchConfig( launchConfigName );
  }

  @Override
  protected void storeItemToMemento( Object item, IMemento memento ) {
    ILaunchConfiguration launchConfig = ( ILaunchConfiguration )item;
    memento.putString( TAG_NAME, launchConfig.getName() );
  }

  private ILaunchConfiguration findLaunchConfig( String launchConfigName ) {
    ILaunchConfiguration result = null;
    for( ILaunchConfiguration launchConf : getLaunchConfigurations() ) {
      if( Objects.equal( launchConf.getName(), launchConfigName ) ) {
        result = launchConf;
      }
    }
    return result;
  }

  private ILaunchConfiguration[] getLaunchConfigurations() {
    ILaunchConfiguration[] result = new ILaunchConfiguration[ 0 ];
    try {
      result = launchManager.getLaunchConfigurations();
    } catch( CoreException ignore ) {
    }
    return result;
  }
}