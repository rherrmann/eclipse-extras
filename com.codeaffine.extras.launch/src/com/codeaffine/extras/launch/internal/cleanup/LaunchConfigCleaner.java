package com.codeaffine.extras.launch.internal.cleanup;

import static com.codeaffine.extras.launch.internal.LaunchExtrasPlugin.PLUGIN_ID;
import static java.util.Collections.addAll;
import static java.util.Collections.synchronizedSet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsDialog;
import org.eclipse.ui.statushandlers.StatusManager;

import com.codeaffine.extras.launch.internal.util.LaunchAdapter;


@SuppressWarnings("restriction")
public class LaunchConfigCleaner {

  private final LaunchPreferences launchPreferences;
  private final ILaunchManager launchManager;
  private final Set<ILaunchConfiguration> cleanupLaunchConfigs;
  private final CleanupLaunchConfigListener cleanupLaunchConfigListener;
  private final CleanupLaunchListener cleanupLaunchListener;

  public LaunchConfigCleaner() {
    this( new LaunchPreferences() );
  }

  public LaunchConfigCleaner( LaunchPreferences launchPreferences ) {
    this.launchPreferences = launchPreferences;
    this.launchManager = DebugPlugin.getDefault().getLaunchManager();
    this.cleanupLaunchConfigs = synchronizedSet( new HashSet<>() );
    this.cleanupLaunchConfigListener = new CleanupLaunchConfigListener();
    this.cleanupLaunchListener = new CleanupLaunchListener();
  }

  public void install() {
    launchManager.addLaunchConfigurationListener( cleanupLaunchConfigListener );
    launchManager.addLaunchListener( cleanupLaunchListener );
  }

  public void uninstall() {
    launchManager.removeLaunchConfigurationListener( cleanupLaunchConfigListener );
    launchManager.removeLaunchListener( cleanupLaunchListener );
    cleanupLaunchConfigs.clear();
  }

  public void saveState( File file ) {
    ILaunchConfiguration[] launchConfigs = cleanupLaunchConfigs.toArray( new ILaunchConfiguration[ 0 ] );
    new LaunchConfigCleanerState( launchManager, file ).save( launchConfigs );
  }

  public void restoreState( File file ) {
    ILaunchConfiguration[] launchConfigs = new LaunchConfigCleanerState( launchManager, file ).restore();
    addAll( cleanupLaunchConfigs, launchConfigs );
  }

  private static void handleCoreException( CoreException exception ) {
    StatusManager.getManager().handle( exception, PLUGIN_ID );
  }

  private class CleanupLaunchListener extends LaunchAdapter {
    @Override
    public void launchesTerminated( ILaunch[] launches ) {
      for( ILaunch launch : launches ) {
        launchTerminated( launch );
      }
    }

    private void launchTerminated( ILaunch launch ) {
      ILaunchConfiguration terminatedLaunchConfig = launch.getLaunchConfiguration();
      if( terminatedLaunchConfig != null ) {
        try {
          launchTerminated( terminatedLaunchConfig );
        } catch( CoreException ce ) {
          handleCoreException( ce );
        }
      }
    }

    private void launchTerminated( ILaunchConfiguration terminatedLaunchConfig ) throws CoreException {
      for( ILaunchConfiguration launchConfig : new ArrayList<>( cleanupLaunchConfigs ) ) {
        if(    launchConfig.getType().equals( terminatedLaunchConfig.getType() )
            && !launchConfig.equals( terminatedLaunchConfig ) )
        {
          cleanupLaunchConfigs.remove( launchConfig );
          launchConfig.delete();
        }
      }
    }
  }

  private class CleanupLaunchConfigListener implements ILaunchConfigurationListener {
    @Override
    public void launchConfigurationAdded( ILaunchConfiguration launchConfig ) {
      try {
        if(    launchPreferences.isCleanupGeneratedLaunchConfigs()
            && !isLaunchConfigsDialogOpen()
            && shouldCleanupLaunchConfig( launchConfig ) )
        {
          cleanupLaunchConfigs.add( launchConfig );
        }
      } catch( CoreException ce ) {
        handleCoreException( ce );
      }
    }

    @Override
    public void launchConfigurationChanged( ILaunchConfiguration launchConfig ) {
      if( isLaunchConfigsDialogOpen() ) {
        cleanupLaunchConfigs.remove( launchConfig );
      }
    }

    @Override
    public void launchConfigurationRemoved( ILaunchConfiguration launchConfig ) {
      cleanupLaunchConfigs.remove( launchConfig );
    }

    private boolean shouldCleanupLaunchConfig( ILaunchConfiguration configuration ) throws CoreException {
      String typeId = configuration.getType().getIdentifier();
      ILaunchConfigurationType[] types = getCleanupGenerateLaunchConfigTypes();
      return Stream.of( types ).anyMatch( type -> Objects.equals( type.getIdentifier(), typeId ) );
    }

    private ILaunchConfigurationType[] getCleanupGenerateLaunchConfigTypes() {
      String typeIds = launchPreferences.getCleanupGenerateLaunchConfigTypes();
      return new LaunchConfigTypeSerializer( launchManager ).deserialize( typeIds );
    }

    private boolean isLaunchConfigsDialogOpen() {
      return LaunchConfigurationsDialog.getCurrentlyVisibleLaunchConfigurationDialog() != null;
    }
  }

}
