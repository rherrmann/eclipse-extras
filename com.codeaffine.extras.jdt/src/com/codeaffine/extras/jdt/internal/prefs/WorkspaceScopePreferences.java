package com.codeaffine.extras.jdt.internal.prefs;

import org.eclipse.jface.preference.IPreferenceStore;

import com.codeaffine.extras.jdt.internal.JDTExtrasPlugin;


public class WorkspaceScopePreferences {

  public static final String PREF_SHOW_JUNIT_STATUS_BAR = "show_junit_status_bar";

  public static void initializeDefaults( IPreferenceStore preferenceStore ) {
    preferenceStore.setDefault( PREF_SHOW_JUNIT_STATUS_BAR, true );
  }

  private final IPreferenceStore preferenceStore;

  public WorkspaceScopePreferences() {
    this( JDTExtrasPlugin.getInstance().getPreferenceStore() );
  }

  public WorkspaceScopePreferences( IPreferenceStore preferenceStore ) {
    this.preferenceStore = preferenceStore;
  }

  public IPreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  public void setShowJUnitStatusBar( boolean value ) {
    preferenceStore.setValue( PREF_SHOW_JUNIT_STATUS_BAR, value );
  }

  public boolean getShowJUnitStatusBar() {
    return preferenceStore.getBoolean( PREF_SHOW_JUNIT_STATUS_BAR );
  }
}