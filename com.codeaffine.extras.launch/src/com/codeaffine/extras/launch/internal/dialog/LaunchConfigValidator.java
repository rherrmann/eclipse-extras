package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.internal.LaunchExtrasPlugin.PLUGIN_ID;
import static java.text.MessageFormat.format;
import static org.eclipse.core.runtime.IStatus.INFO;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;

import com.codeaffine.extras.launch.internal.LaunchExtrasPlugin;


public class LaunchConfigValidator {

  private final ILaunchConfiguration launchConfig;
  private final ILaunchMode preferredLaunchMode;

  public LaunchConfigValidator( ILaunchConfiguration launchConfig, ILaunchMode preferredLaunchMode ) {
    this.launchConfig = launchConfig;
    this.preferredLaunchMode = preferredLaunchMode;
  }

  public IStatus validate() {
    IStatus result = okStatus();
    ILaunchMode launchMode = computeActualLaunchMode();
    if( !preferredLaunchMode.getIdentifier().equals( launchMode.getIdentifier() ) ) {
      result = new Status( INFO, PLUGIN_ID, INFO, getLaunchModeWarningMessage( launchMode ), null );
    }
    return result;
  }

  private ILaunchMode computeActualLaunchMode() {
    return new LaunchModeComputer( launchConfig, preferredLaunchMode ).computeLaunchMode();
  }

  private static String getLaunchModeWarningMessage( ILaunchMode launchMode ) {
    String label = launchMode.getLabel().replace( "&", "" );
    return format( "Selection will be launched in ''{0}'' mode", label );
  }

  private static Status okStatus() {
    return new Status( IStatus.OK, LaunchExtrasPlugin.PLUGIN_ID, "" );
  }

}
