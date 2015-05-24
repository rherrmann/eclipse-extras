package com.codeaffine.extras.launch.internal;

import static java.util.Arrays.asList;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.debug.ui.actions.AbstractLaunchHistoryAction;
import org.eclipse.ui.IMemento;

import com.codeaffine.extras.launch.internal.LaunchSelectionDialog.AccessibleSelectionHistory;

class LaunchConfigSelectionHistory extends AccessibleSelectionHistory {

  @Override
  public synchronized boolean contains( Object object ) {
    return asList( getLaunchConfigHistory() ).contains( object );
  }

  @Override
  public synchronized Object[] getHistoryItems() {
    return getLaunchConfigHistory();
  }

  @Override
  public synchronized boolean remove( Object element ) {
    return false;
  }

  @Override
  public synchronized boolean isEmpty() {
    return getLaunchConfigHistory().length == 0;
  }

  @Override
  protected Object restoreItemFromMemento( IMemento memento ) {
    return null;
  }

  @Override
  protected void storeItemToMemento( Object item, IMemento memento ) {
  }

  private static ILaunchConfiguration[] getLaunchConfigHistory() {
    return new LaunchConfigHistoryCollector().collect();
  }

  private static class LaunchConfigHistoryCollector {
    private final Set<ILaunchConfiguration> launchConfigHistory;

    LaunchConfigHistoryCollector() {
      launchConfigHistory = new LinkedHashSet<ILaunchConfiguration>();
    }

    ILaunchConfiguration[] collect() {
      for( ILaunchGroup launchGroup : DebugUITools.getLaunchGroups() ) {
        collect( launchGroup );
      }
      return launchConfigHistory.toArray( new ILaunchConfiguration[ launchConfigHistory.size() ] );
    }

    private void collect( ILaunchGroup launchGroup ) {
      LaunchHistoryAction launchHistoryAction = new LaunchHistoryAction( launchGroup.getIdentifier() );
      try {
        if( launchHistoryAction.getLastLaunch() != null ) {
          append( launchHistoryAction.getHistory() );
          append( launchHistoryAction.getFavorites() );
        }
      } finally {
        launchHistoryAction.dispose();
      }
    }

    private void append( ILaunchConfiguration... launchConfigs ) {
      launchConfigHistory.addAll( asList( launchConfigs ) );
    }
  }

  private static class LaunchHistoryAction extends AbstractLaunchHistoryAction {
    LaunchHistoryAction( String launchGroupId ) {
      super( launchGroupId );
    }

    @Override
    public ILaunchConfiguration getLastLaunch() {
      return super.getLastLaunch();
    }

    @Override
    public ILaunchConfiguration[] getFavorites() {
      return super.getFavorites();
    }

    @Override
    public ILaunchConfiguration[] getHistory() {
      return super.getHistory();
    }
  }
}