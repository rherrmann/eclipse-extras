package com.codeaffine.extras.jdt.internal.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.codeaffine.extras.jdt.internal.JDTExtrasPlugin;


public class WorkspaceScopePreferenceInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore preferenceStore = JDTExtrasPlugin.getInstance().getPreferenceStore();
    WorkspaceScopePreferences.initializeDefaults( preferenceStore );
  }
}
