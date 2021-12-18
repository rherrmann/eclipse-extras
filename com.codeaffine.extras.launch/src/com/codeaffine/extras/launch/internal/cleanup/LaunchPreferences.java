package com.codeaffine.extras.launch.internal.cleanup;

import static java.util.Objects.requireNonNull;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.codeaffine.extras.launch.internal.LaunchExtrasPlugin;


public class LaunchPreferences {

  public static final String PREF_CLEANUP_TYPES = LaunchExtrasPlugin.PLUGIN_ID + ".CleanUpLaunchConfigurationTypes";
  public static final String PREF_CLEANUP = LaunchExtrasPlugin.PLUGIN_ID + ".CleanUpLaunchConfigurations";

  public static IPreferenceStore getPluginPreferenceStore() {
    return new ScopedPreferenceStore(InstanceScope.INSTANCE, LaunchExtrasPlugin.PLUGIN_ID);
  }

  private final IPreferenceStore preferenceStore;

  public LaunchPreferences() {
    this(getPluginPreferenceStore());
  }

  public LaunchPreferences(IPreferenceStore preferenceStore) {
    this.preferenceStore = requireNonNull(preferenceStore, "preferenceStore");
  }

  public IPreferenceStore getPreferenceStore() {
    return preferenceStore;
  }

  public void setCleanupGeneratedLaunchConfigs(boolean value) {
    preferenceStore.setValue(PREF_CLEANUP, value);
  }

  public boolean isCleanupGeneratedLaunchConfigs() {
    return preferenceStore.getBoolean(PREF_CLEANUP);
  }

  public void setCleanupGenerateLaunchConfigTypes(String typeIds) {
    preferenceStore.setValue(PREF_CLEANUP_TYPES, typeIds);
  }

  public String getCleanupGenerateLaunchConfigTypes() {
    return preferenceStore.getString(PREF_CLEANUP_TYPES);
  }

}
