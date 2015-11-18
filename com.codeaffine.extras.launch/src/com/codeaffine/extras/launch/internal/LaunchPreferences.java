package com.codeaffine.extras.launch.internal;

import static java.util.Objects.requireNonNull;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


public class LaunchPreferences {

  private static final String DEBUG_PLUGIN_ID = IDebugUIConstants.PLUGIN_ID;

  static final String PREF_FILTER_LAUNCH_CLOSED = DEBUG_PLUGIN_ID + ".PREF_FILTER_LAUNCH_CLOSED";
  static final String PREF_FILTER_LAUNCH_DELETED = DEBUG_PLUGIN_ID + ".PREF_FILTER_LAUNCH_DELETED";

  private final IPreferenceStore preferenceStore;

  public LaunchPreferences() {
    this( getDebugPreferenceStore() );
  }

  public IPreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  public LaunchPreferences( IPreferenceStore preferenceStore ) {
    this.preferenceStore = requireNonNull( preferenceStore, "preferenceStore" );
  }

  public void setFilterLaunchConfigsInClosedProjects( boolean value ) {
    preferenceStore.setValue( PREF_FILTER_LAUNCH_CLOSED, value );
  }

  public boolean isFilterLaunchConfigsInClosedProjects() {
    return preferenceStore.getBoolean( PREF_FILTER_LAUNCH_CLOSED );
  }

  public void setFilterLaunchConfigsInDeletedProjects( boolean value ) {
    preferenceStore.setValue( PREF_FILTER_LAUNCH_DELETED, value );
  }

  public boolean isFilterLaunchConfigsInDeletedProjects() {
    return preferenceStore.getBoolean( PREF_FILTER_LAUNCH_DELETED );
  }

  private static IPreferenceStore getDebugPreferenceStore() {
    return new ScopedPreferenceStore( InstanceScope.INSTANCE, DEBUG_PLUGIN_ID );
  }

}
