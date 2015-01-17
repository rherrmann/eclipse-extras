package com.codeaffine.extras.platform.internal.launch;

import static com.google.common.collect.Iterables.indexOf;
import static java.util.Arrays.asList;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;

import java.util.List;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.dialogs.IDialogSettings;

import com.google.common.base.Predicate;


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
    List<ILaunchMode> launchModes = asList( launchManager.getLaunchModes() );
    return indexOf( launchModes, new MatchesLaunchModeIdentifier( launchModeId ) ) > -1;
  }

  private static class MatchesLaunchModeIdentifier implements Predicate<ILaunchMode> {
    private final String launchModeId;

    MatchesLaunchModeIdentifier( String launchModeId ) {
      this.launchModeId = launchModeId;
    }

    @Override
    public boolean apply( ILaunchMode input ) {
      return input.getIdentifier().equals( launchModeId );
    }
  }

}
