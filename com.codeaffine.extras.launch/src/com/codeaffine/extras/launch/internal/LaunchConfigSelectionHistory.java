package com.codeaffine.extras.launch.internal;

import static java.util.Arrays.asList;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchHistory;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.debug.ui.actions.AbstractLaunchHistoryAction;
import org.eclipse.ui.IMemento;

import com.codeaffine.extras.launch.internal.LaunchSelectionDialog.AccessibleSelectionHistory;

@SuppressWarnings("restriction")
public class LaunchConfigSelectionHistory extends AccessibleSelectionHistory {

  private ILaunchConfiguration[] launchConfigHistory;

  @Override
  public synchronized boolean contains( Object object ) {
    return asList( getLaunchConfigHistory() ).contains( object );
  }

  @Override
  public synchronized Object[] getHistoryItems() {
    return getLaunchConfigHistory().clone();
  }

  @Override
  public synchronized boolean remove( Object element ) {
    boolean removed = false;
    if( contains( element ) ) {
      for( ILaunchGroup launchGroup : DebugUITools.getLaunchGroups() ) {
        LaunchHistory launchHistory = getLaunchHistory( launchGroup );
        if( launchHistory != null ) {
          launchHistory.removeFromHistory( ( ILaunchConfiguration )element );
        }
      }
      removed = true;
      launchConfigHistory = null;
    }
    return removed;
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

  private ILaunchConfiguration[] getLaunchConfigHistory() {
    if( launchConfigHistory == null ) {
      launchConfigHistory = new LaunchConfigHistoryCollector().collect();
    }
    return launchConfigHistory;
  }

  private static LaunchHistory getLaunchHistory( ILaunchGroup launchGroup ) {
    LaunchHistoryAction launchHistoryAction = new LaunchHistoryAction( launchGroup );
    try {
      return launchHistoryAction.getLaunchHistory();
    } finally {
      launchHistoryAction.dispose();
    }
  }

  private static class LaunchConfigHistoryCollector {
    private final Set<ILaunchConfiguration> launchConfigHistory;

    LaunchConfigHistoryCollector() {
      launchConfigHistory = new LinkedHashSet<ILaunchConfiguration>();
    }

    ILaunchConfiguration[] collect() {
      launchConfigHistory.clear();
      for( ILaunchGroup launchGroup : DebugUITools.getLaunchGroups() ) {
        collect( launchGroup );
      }
      return launchConfigHistory.toArray( new ILaunchConfiguration[ launchConfigHistory.size() ] );
    }

    private void collect( ILaunchGroup launchGroup ) {
      LaunchHistoryAction launchHistoryAction = new LaunchHistoryAction( launchGroup );
      try {
        if( launchHistoryAction.getLastLaunch() != null ) {
          append( launchHistoryAction.getFavorites() );
          append( launchHistoryAction.getHistory() );
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
    LaunchHistoryAction( ILaunchGroup launchGroup ) {
      super( launchGroup.getIdentifier() );
    }

    @SuppressWarnings("deprecation")
    @Override
    public LaunchHistory getLaunchHistory() {
      return super.getLaunchHistory();
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