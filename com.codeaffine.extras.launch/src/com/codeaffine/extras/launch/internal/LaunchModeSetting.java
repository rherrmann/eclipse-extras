package com.codeaffine.extras.launch.internal;

import static java.util.Arrays.stream;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.dialogs.IDialogSettings;


public class LaunchModeSetting {

  private static final String SETTING_LAUNCH_MODE = "launchMode";

  private final ILaunchManager launchManager;
  private final IDialogSettings settings;

  public LaunchModeSetting( ILaunchManager launchManager, IDialogSettings settings ) {
    this.launchManager = launchManager;
    this.settings = settings;
  }

  public ILaunchManager getLaunchManager() {
    return launchManager;
  }

  public String getLaunchModeId() {
    String result = DEBUG_MODE;
    String storedLaunchMode = settings.get( SETTING_LAUNCH_MODE );
    if( isValidLaunchMode( storedLaunchMode ) ) {
      result = storedLaunchMode;
    }
    return result;
  }

  public ILaunchMode getLaunchMode() {
    return launchManager.getLaunchMode( getLaunchModeId() );
  }

  public void setLaunchModeId( String launchModeId ) {
    settings.put( SETTING_LAUNCH_MODE, launchModeId );
  }

  private boolean isValidLaunchMode( String launchModeId ) {
    ILaunchMode[] launchModes = launchManager.getLaunchModes();
    return stream( launchModes ).anyMatch( input -> input.getIdentifier().equals( launchModeId ) );
  }

}
