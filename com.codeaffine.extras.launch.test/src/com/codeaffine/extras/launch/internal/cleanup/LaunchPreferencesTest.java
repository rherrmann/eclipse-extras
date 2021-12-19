package com.codeaffine.extras.launch.internal.cleanup;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.Before;
import org.junit.Test;

public class LaunchPreferencesTest {

  private IPreferenceStore preferenceStore;
  private LaunchPreferences launchPreferences;

  @Before
  public void setUp() {
    preferenceStore = new PreferenceStore();
    launchPreferences = new LaunchPreferences(preferenceStore);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullPreferenceStore() {
    new LaunchPreferences(null);
  }

  @Test
  public void testGetPreferenceStore() {
    IPreferenceStore preferenceStore = new LaunchPreferences().getPreferenceStore();

    assertThat(preferenceStore).isInstanceOf(ScopedPreferenceStore.class);
  }

  @Test
  public void testSetCleanupGeneratedLaunchConfigs() {
    launchPreferences.setCleanupGeneratedLaunchConfigs(true);

    assertThat(launchPreferences.isCleanupGeneratedLaunchConfigs()).isTrue();
  }

  @Test
  public void testIsCleanupGeneratedLaunchConfigs() {
    preferenceStore.setValue(LaunchPreferences.PREF_CLEANUP, true);

    assertThat(launchPreferences.isCleanupGeneratedLaunchConfigs()).isTrue();
  }

  @Test
  public void testSetCleanupGenerateLaunchConfigTypes() {
    String typeIds = "typeIds";
    launchPreferences.setCleanupGenerateLaunchConfigTypes(typeIds);

    assertThat(launchPreferences.getCleanupGenerateLaunchConfigTypes()).isEqualTo(typeIds);
  }

  @Test
  public void testGetCleanupGenerateLaunchConfigTypes() {
    String typeIds = "typeIds";
    preferenceStore.setValue(LaunchPreferences.PREF_CLEANUP_TYPES, typeIds);

    assertThat(launchPreferences.getCleanupGenerateLaunchConfigTypes()).isEqualTo(typeIds);
  }

}
