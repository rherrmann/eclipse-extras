package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.internal.LaunchExtrasPlugin.PLUGIN_ID;
import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.IStatus.INFO;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;


public class LaunchConfigValidator {

  static final String NO_LAUNCH_MODE_FOUND = "No launch mode could be found";
  static final String LAUNCH_CONFIG_NOT_FOUND = "Launch configuration could not be found";

  private final ILaunchConfiguration launchConfig;
  private final ILaunchMode preferredLaunchMode;

  public LaunchConfigValidator( ILaunchConfiguration launchConfig, ILaunchMode preferredLaunchMode ) {
    requireNonNull( launchConfig, "launchConfig" );
    this.launchConfig = launchConfig;
    this.preferredLaunchMode = preferredLaunchMode;
  }

  public IStatus validate() {
    IStatus result = okStatus();
    if( launchConfig.exists() ) {
      result = validateExistingLaunchConfig();
    } else {
      result = new Status( ERROR, PLUGIN_ID, 0, LAUNCH_CONFIG_NOT_FOUND, null );
    }
    return result;
  }

  private IStatus validateExistingLaunchConfig() {
    IStatus result = okStatus();
    ILaunchMode launchMode = computeActualLaunchMode();
    if( launchMode == null ) {
      result = new Status( ERROR, PLUGIN_ID, 0, NO_LAUNCH_MODE_FOUND, null );
    } else if( !preferredLaunchMode.getIdentifier().equals( launchMode.getIdentifier() ) ) {
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
    return new Status( IStatus.OK, PLUGIN_ID, "" );
  }

}
