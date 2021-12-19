package com.codeaffine.extras.launch.internal.dialog;

import static com.codeaffine.extras.launch.internal.dialog.DebugUIPreferences.PREF_TERMINATE_AND_RELAUNCH;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.junit.Before;
import org.junit.Test;

public class DebugUIPreferencesTest {

  private PreferenceStore preferenceStore;

  @Before
  public void setUp() {
    preferenceStore = new PreferenceStore();
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullArgument() {
    new DebugUIPreferences(null);
  }

  @Test
  public void testGetPreferenceStore() {
    DebugUIPreferences debugUIPreferences = new DebugUIPreferences(preferenceStore);

    IPreferenceStore returnedPreferenceStore = debugUIPreferences.getPreferenceStore();

    assertThat(returnedPreferenceStore).isEqualTo(preferenceStore);
  }

  @Test
  public void testSetAndGetTerminateBeforeRelaunch() {
    DebugUIPreferences debugUIPreferences = new DebugUIPreferences(preferenceStore);
    debugUIPreferences.setTerminateBeforeRelaunch(true);

    boolean terminateBeforeRelaunch = debugUIPreferences.isTerminateBeforeRelaunch();

    assertThat(terminateBeforeRelaunch).isTrue();
  }

  @Test
  public void testSetTerminateBeforeRelaunch() {
    DebugUIPreferences debugUIPreferences = new DebugUIPreferences(preferenceStore);

    debugUIPreferences.setTerminateBeforeRelaunch(true);

    assertThat(preferenceStore.getBoolean(PREF_TERMINATE_AND_RELAUNCH)).isTrue();
  }
}
