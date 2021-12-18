package com.codeaffine.extras.launch.internal.dialog;

import static java.util.Objects.requireNonNull;

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class DebugUIPreferences {

  static final String PREF_TERMINATE_AND_RELAUNCH = IDebugUIConstants.PLUGIN_ID + ".RelaunchAndTerminateLaunchAction";

  private final IPreferenceStore preferenceStore;

  public DebugUIPreferences() {
    this(DebugUITools.getPreferenceStore());
  }

  public DebugUIPreferences(IPreferenceStore preferenceStore) {
    this.preferenceStore = requireNonNull(preferenceStore);
  }

  public IPreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  public boolean isTerminateBeforeRelaunch() {
    return preferenceStore.getBoolean(PREF_TERMINATE_AND_RELAUNCH);
  }

  public void setTerminateBeforeRelaunch(boolean value) {
    preferenceStore.setValue(PREF_TERMINATE_AND_RELAUNCH, value);
  }
}
